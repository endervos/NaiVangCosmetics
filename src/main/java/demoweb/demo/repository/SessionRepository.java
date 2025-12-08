package demoweb.demo.repository;

import demoweb.demo.entity.Account;
import demoweb.demo.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    List<Session> findByAccount(Account account);
}