package demoweb.demo.repository;

import demoweb.demo.entity.Account;
import demoweb.demo.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    @Query("SELECT s FROM Session s WHERE s.token = :token AND s.endTime IS NULL ORDER BY s.startTime DESC")
    List<Session> findSessionsByToken(@Param("token") String token);

    default Optional<Session> findByToken(String token) {
        List<Session> sessions = findSessionsByToken(token);
        return sessions.isEmpty() ? Optional.empty() : Optional.of(sessions.get(0));
    }

    @Query("SELECT s FROM Session s WHERE s.token = :token AND s.endTime IS NULL ORDER BY s.startTime DESC")
    List<Session> findAllByToken(@Param("token") String token);

    @Query("SELECT s FROM Session s WHERE s.account = :account AND s.endTime IS NULL")
    List<Session> findActiveSessionsByAccount(@Param("account") Account account);

    List<Session> findByAccount(Account account);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.account = :account AND s.endTime IS NULL")
    long countActiveSessionsByAccount(@Param("account") Account account);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.endTime < :currentTime")
    int deleteExpiredSessions(@Param("currentTime") LocalDateTime currentTime);

    @Modifying
    @Query("UPDATE Session s SET s.endTime = :endTime WHERE s.account = :account AND s.endTime IS NULL")
    int closeAllSessionsByAccount(@Param("account") Account account, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM Session s WHERE s.sessionId = :sessionId AND s.endTime IS NULL")
    Optional<Session> findActiveSessionById(@Param("sessionId") String sessionId);
}