package demoweb.demo.service;

import demoweb.demo.entity.Account;
import demoweb.demo.entity.FailedLogin;
import demoweb.demo.repository.AccountRepository;
import demoweb.demo.repository.FailedLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FailedLoginService {

    @Autowired
    private FailedLoginRepository failedLoginRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 10;
    private static final int FAILED_ATTEMPT_WINDOW_MINUTES = 5;

    @Transactional
    public void recordFailedLogin(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        FailedLogin failedLogin = new FailedLogin();
        failedLogin.setAccount(account);
        failedLogin.setLoginTime(LocalDateTime.now());
        failedLogin.setStatus("Fail");
        failedLoginRepository.save(failedLogin);
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(FAILED_ATTEMPT_WINDOW_MINUTES);
        List<FailedLogin> recentFailedAttempts = failedLoginRepository
                .findByAccountAndLoginTimeAfterAndStatus(account, fiveMinutesAgo, "Fail");
        if (recentFailedAttempts.size() >= MAX_FAILED_ATTEMPTS) {
            account.setIsActive(false);
            accountRepository.save(account);
        }
    }

    @Transactional
    public void recordSuccessfulLogin(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        FailedLogin successLogin = new FailedLogin();
        successLogin.setAccount(account);
        successLogin.setLoginTime(LocalDateTime.now());
        successLogin.setStatus("Success");
        failedLoginRepository.save(successLogin);
    }

    public boolean isAccountLocked(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!account.getIsActive()) {
            FailedLogin lastFailedLogin = failedLoginRepository
                    .findTopByAccountAndStatusOrderByLoginTimeDesc(account, "Fail");
            if (lastFailedLogin != null) {
                LocalDateTime lockoutTime = lastFailedLogin.getLoginTime();
                LocalDateTime unlockTime = lockoutTime.plusMinutes(LOCKOUT_DURATION_MINUTES);
                if (LocalDateTime.now().isAfter(unlockTime)) {
                    account.setIsActive(true);
                    accountRepository.save(account);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public long getRemainingLockoutTime(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!account.getIsActive()) {
            FailedLogin lastFailedLogin = failedLoginRepository
                    .findTopByAccountAndStatusOrderByLoginTimeDesc(account, "Fail");
            if (lastFailedLogin != null) {
                LocalDateTime lockoutTime = lastFailedLogin.getLoginTime();
                LocalDateTime unlockTime = lockoutTime.plusMinutes(LOCKOUT_DURATION_MINUTES);
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(unlockTime)) {
                    return java.time.Duration.between(now, unlockTime).toMinutes();
                }
            }
        }
        return 0;
    }

    public int getFailedAttemptsCount(String accountId, int withinMinutes) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(withinMinutes);
        List<FailedLogin> recentFailedAttempts = failedLoginRepository
                .findByAccountAndLoginTimeAfterAndStatus(account, timeThreshold, "Fail");
        return recentFailedAttempts.size();
    }
}