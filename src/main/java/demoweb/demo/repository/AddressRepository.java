package demoweb.demo.repository;

import demoweb.demo.entity.Address;
import demoweb.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCustomer(Customer customer);

    List<Address> findByCustomerAndIsDeleted(Customer customer, Integer isDeleted);

    Optional<Address> findByCustomerAndIdAddressDefaultTrue(Customer customer);
}