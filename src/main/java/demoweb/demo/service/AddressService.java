package demoweb.demo.service;

import demoweb.demo.entity.Address;
import demoweb.demo.entity.Customer;
import demoweb.demo.repository.AddressRepository;
import demoweb.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    public List<Address> getAddressesByUserId(String userId) {
        var customerOpt = customerRepository.findByUser_UserId(userId);
        if (customerOpt.isEmpty()) {
            return List.of();
        }
        Customer customer = customerOpt.get();
        var addresses = addressRepository.findByCustomer(customer);
        return addresses;
    }

    public Address getDefaultAddress(String userId) {
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        return addressRepository.findByCustomerAndIdAddressDefaultTrue(customer).orElse(null);
    }

    @Transactional
    public void addAddress(String userId, String street, String district, String city,
                           String phoneNumber, boolean isDefault) {
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng để thêm địa chỉ"));
        if (isDefault) removeDefaultFlag(customer);
        Address address = new Address();
        address.setCustomer(customer);
        address.setStreet(street);
        address.setDistrict(district);
        address.setCity(city);
        address.setPhoneNumber(phoneNumber);
        address.setIdAddressDefault(true);
        addressRepository.save(address);
    }

    @Transactional
    public void updateAddress(Integer addressId, String userId,
                              String street, String district, String city,
                              String phoneNumber, boolean isDefault) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ có ID = " + addressId));
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        if (!address.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("Không thể chỉnh sửa địa chỉ của người khác");
        }
        if (isDefault) {
            removeDefaultFlag(customer);
            address.setIdAddressDefault(true);
        }
        address.setStreet(street);
        address.setDistrict(district);
        address.setCity(city);
        address.setPhoneNumber(phoneNumber);
        addressRepository.save(address);
    }

    @Transactional
    public void setDefaultAddress(String userId, Integer addressId) {
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        removeDefaultFlag(customer);
        Address target = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        if (target.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            target.setIdAddressDefault(true);
            addressRepository.save(target);
        } else {
            throw new RuntimeException("⚠️ Không thể đặt mặc định cho địa chỉ của người khác");
        }
    }

    @Transactional
    public void deleteAddress(String userId, Integer addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ để xóa"));
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        if (!address.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("Không thể xóa địa chỉ của người khác");
        }
        addressRepository.delete(address);
    }

    @Transactional
    protected void removeDefaultFlag(Customer customer) {
        List<Address> addresses = addressRepository.findByCustomer(customer);
        for (Address addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIdAddressDefault())) {
                addr.setIdAddressDefault(false);
                addressRepository.save(addr);
            }
        }
    }
}
