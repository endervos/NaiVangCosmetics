package demoweb.demo.repository;

import demoweb.demo.entity.Customer;
import demoweb.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUser(User user);

    Optional<Customer> findByUser_UserId(String userId);
}