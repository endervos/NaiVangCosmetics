package demoweb.demo.service;

import demoweb.demo.entity.*;
import demoweb.demo.repository.AccountRepository;
import demoweb.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUser_Email(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));
        return User.builder()
                .username(account.getUser().getEmail())
                .password(account.getUser().getPassword())
                .roles(account.getRole().getName())
                .build();
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account findById(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
    }

    @Transactional
    public Account updateAccount(String accountId, String fullname, String birthday,
                                 String gender, String phoneNumber, String roleName) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        demoweb.demo.entity.User user = account.getUser();

        if (fullname != null && !fullname.trim().isEmpty()) {
            user.setFullname(fullname.trim());
        }
        if (birthday != null && !birthday.trim().isEmpty()) {
            user.setBirthday(LocalDate.parse(birthday));
        }
        if (gender != null && !gender.trim().isEmpty()) {
            user.setGender(Gender.valueOf(gender));
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber.trim());
        }
        if (roleName != null && !roleName.equals(account.getRole().getName())) {
            Role newRole = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò: " + roleName));
            account.setRole(newRole);
        }

        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        accountRepository.delete(account);
    }
}