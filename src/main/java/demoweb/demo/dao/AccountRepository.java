package demoweb.demo.dao;

import demoweb.demo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByAccountId(String accountId);

    Account findByUsername(String username);

    Account findByAccountId(String accountId);
}
