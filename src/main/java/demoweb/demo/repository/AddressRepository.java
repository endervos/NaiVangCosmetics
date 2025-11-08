package demoweb.demo.repository;

import demoweb.demo.entity.Address;
import demoweb.demo.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByCustomer(Customer customer);
    Optional<Address> findByCustomerAndIsDefaultTrue(Customer customer);

    //lay user
    @Query("SELECT a FROM Address a WHERE a.customer.user.userId = :userId AND a.isDeleted = false")
    List<Address> findAllByUserId(@Param("userId") String userId);
}