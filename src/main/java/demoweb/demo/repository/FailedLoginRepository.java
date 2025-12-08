package demoweb.demo.repository;

import demoweb.demo.entity.Account;
import demoweb.demo.entity.FailedLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FailedLoginRepository extends JpaRepository<FailedLogin, Long> {

    List<FailedLogin> findByAccountAndLoginTimeAfterAndStatus(Account account, LocalDateTime loginTime, String status);

    FailedLogin findTopByAccountAndStatusOrderByLoginTimeDesc(Account account, String status);
}