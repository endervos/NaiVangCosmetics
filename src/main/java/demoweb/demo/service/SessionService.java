package demoweb.demo.service;

import demoweb.demo.entity.Account;
import demoweb.demo.entity.Session;
import demoweb.demo.repository.AccountRepository;
import demoweb.demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Session createSession(String accountId, String token) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setAccount(account);
        session.setToken(token);
        session.setStartTime(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public Optional<Session> findByToken(String token) {
        List<Session> sessions = sessionRepository.findAllByToken(token);
        return sessions.isEmpty() ? Optional.empty() : Optional.of(sessions.get(0));
    }

    public Optional<Session> findActiveSessionById(String sessionId) {
        return sessionRepository.findActiveSessionById(sessionId);
    }

    @Transactional
    public Session updateSessionToken(String sessionId, String newToken) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session không tồn tại"));
        List<Session> oldSessions = sessionRepository.findSessionsByToken(newToken);
        for (Session oldSession : oldSessions) {
            if (!oldSession.getSessionId().equals(sessionId)) {
                sessionRepository.delete(oldSession);
            }
        }
        session.setToken(newToken);
        return sessionRepository.save(session);
    }

    @Transactional
    public void closeSession(String sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session không tồn tại"));
        session.setEndTime(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Transactional
    public void closeSessionByToken(String token) {
        List<Session> sessions = sessionRepository.findAllByToken(token);
        if (!sessions.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Session session : sessions) {
                session.setEndTime(now);
                sessionRepository.save(session);
            }
        }
    }

    @Transactional
    public int closeAllSessionsByAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
        return sessionRepository.closeAllSessionsByAccount(account, LocalDateTime.now());
    }

    public List<Session> getActiveSessionsByAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
        return sessionRepository.findActiveSessionsByAccount(account);
    }

    public List<Session> getAllSessionsByAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
        return sessionRepository.findByAccount(account);
    }

    public long countActiveSessionsByAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
        return sessionRepository.countActiveSessionsByAccount(account);
    }

    public boolean isSessionActive(String sessionId) {
        Optional<Session> session = sessionRepository.findById(sessionId);
        return session.isPresent() && session.get().getEndTime() == null;
    }

    public boolean isTokenValid(String token) {
        Optional<Session> session = sessionRepository.findByToken(token);
        return session.isPresent() && session.get().getEndTime() == null;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int deletedCount = sessionRepository.deleteExpiredSessions(thirtyDaysAgo);
    }

    @Transactional
    public void deleteSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    public Optional<Session> getSessionById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Transactional
    public void cleanupDuplicateSessions() {
        List<Session> allSessions = sessionRepository.findAll();
        Map<String, List<Session>> sessionsByToken = new HashMap<>();
        for (Session session : allSessions) {
            if (session.getEndTime() == null) {
                sessionsByToken.computeIfAbsent(session.getToken(), k -> new ArrayList<>()).add(session);
            }
        }
        int deletedCount = 0;
        for (Map.Entry<String, List<Session>> entry : sessionsByToken.entrySet()) {
            List<Session> sessions = entry.getValue();
            if (sessions.size() > 1) {
                sessions.sort((s1, s2) -> s2.getStartTime().compareTo(s1.getStartTime()));
                for (int i = 1; i < sessions.size(); i++) {
                    sessionRepository.delete(sessions.get(i));
                    deletedCount++;
                }
            }
        }
    }
}