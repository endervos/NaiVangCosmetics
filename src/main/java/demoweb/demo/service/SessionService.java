package demoweb.demo.service;

import demoweb.demo.entity.Account;
import demoweb.demo.entity.Session;
import demoweb.demo.repository.SessionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Session> getSessionById(String token) {
        return sessionRepository.findById(token);
    }

    @Transactional(readOnly = true)
    public Optional<Session> getSessionByToken(String token) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetSessionsByToken");
        query.registerStoredProcedureParameter(1, String.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter(1, token);
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();
        if (rawResults.isEmpty()) {
            return Optional.empty();
        }
        Object[] row = rawResults.get(0);
        Session session = new Session();
        session.setSessionId((String) row[0]);
        session.setToken((String) row[1]);
        if (row[2] != null) {
            java.sql.Timestamp startTimestamp = (java.sql.Timestamp) row[2];
            session.setStartTime(startTimestamp.toLocalDateTime());
        }
        if (row[3] != null) {
            java.sql.Timestamp endTimestamp = (java.sql.Timestamp) row[3];
            session.setEndTime(endTimestamp.toLocalDateTime());
        }
        if (row[4] != null) {
            Account account = new Account();
            account.setAccountId(String.valueOf(row[4]));
            session.setAccount(account);
        }
        return Optional.of(session);
    }

    @Transactional(readOnly = true)
    public Optional<Session> findByToken(String token) {
        return sessionRepository.findAll().stream()
                .filter(s -> token.equals(s.getToken()))
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<Session> getActiveSessionsByAccountId(Integer accountId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetActiveSessionsByAccount");
        query.registerStoredProcedureParameter(1, Integer.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter(1, accountId);
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();
        List<Session> sessions = new ArrayList<>();
        for (Object[] row : rawResults) {
            Session session = new Session();
            session.setSessionId((String) row[0]);
            session.setToken((String) row[1]);
            if (row[2] != null) {
                java.sql.Timestamp startTimestamp = (java.sql.Timestamp) row[2];
                session.setStartTime(startTimestamp.toLocalDateTime());
            }
            if (row[3] != null) {
                java.sql.Timestamp endTimestamp = (java.sql.Timestamp) row[3];
                session.setEndTime(endTimestamp.toLocalDateTime());
            }
            if (row[4] != null) {
                Account account = new Account();
                account.setAccountId(String.valueOf(row[4]));
                session.setAccount(account);
            }
            sessions.add(session);
        }
        return sessions;
    }

    @Transactional(readOnly = true)
    public List<Session> getActiveSessionsByAccount(String accountId) {
        try {
            Integer id = Integer.parseInt(accountId);
            return getActiveSessionsByAccountId(id);
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Long countActiveSessionsByAccountId(Integer accountId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CountActiveSessionsByAccount");
        query.registerStoredProcedureParameter(1, Integer.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter(1, accountId);
        Object result = query.getSingleResult();
        return ((Number) result).longValue();
    }

    @Transactional(readOnly = true)
    public List<Session> getSessionsByAccount(Account account) {
        return sessionRepository.findByAccount(account);
    }

    @Transactional
    public Session save(Session session) {
        if (session.getSessionId() == null || session.getSessionId().isEmpty()) {
            session.setSessionId(UUID.randomUUID().toString());
        }
        return sessionRepository.save(session);
    }

    @Transactional
    public void delete(String token) {
        sessionRepository.deleteById(token);
    }

    @Transactional
    public void deleteExpiredSessions() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("DeleteExpiredSessions");
        query.execute();
    }

    @Transactional
    public void closeAllSessionsByAccountId(Integer accountId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CloseAllSessionsByAccount");
        query.registerStoredProcedureParameter(1, Integer.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter(1, accountId);
        query.execute();
    }

    @Transactional
    public Session createSession(Account account, LocalDateTime expiresAt) {
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setAccount(account);
        session.setEndTime(expiresAt);
        return sessionRepository.save(session);
    }

    @Transactional
    public Session createSession(String accountId, String token) {
        Account account = new Account();
        account.setAccountId(accountId);
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setAccount(account);
        session.setToken(token);
        session.setEndTime(LocalDateTime.now().plusHours(24));
        return sessionRepository.save(session);
    }

    @Transactional
    public void updateLastAccessed(String token) {
        Optional<Session> sessionOpt = findByToken(token);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            sessionRepository.save(session);
        }
    }

    @Transactional
    public void deactivateSession(String token) {
        Optional<Session> sessionOpt = findByToken(token);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.setEndTime(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    @Transactional
    public void closeSessionByToken(String token) {
        Optional<Session> sessionOpt = findByToken(token);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.setEndTime(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    @Transactional
    public void updateSessionToken(String sessionId, String newToken) {
        Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.setToken(newToken);
            sessionRepository.save(session);
        }
    }

    @Transactional(readOnly = true)
    public boolean isSessionValid(String token) {
        Optional<Session> sessionOpt = findByToken(token);
        if (sessionOpt.isEmpty()) return false;
        Session session = sessionOpt.get();
        return session.getEndTime() == null || session.getEndTime().isAfter(LocalDateTime.now());
    }
}