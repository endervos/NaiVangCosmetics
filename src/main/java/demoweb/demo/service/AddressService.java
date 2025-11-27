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
        System.out.println("🟢 [AddressService] Gọi getAddressesByUserId() userId = " + userId);

        var customerOpt = customerRepository.findByUser_UserId(userId);
        System.out.println("🟢 [AddressService] customerOpt.isPresent() = " + customerOpt.isPresent());

        if (customerOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy Customer cho userId = " + userId);
            return List.of();
        }

        Customer customer = customerOpt.get();
        System.out.println("🟢 [AddressService] customer_id = " + customer.getCustomerId());

        var addresses = addressRepository.findByCustomer(customer);
        System.out.println("🟢 [AddressService] Tìm thấy " + addresses.size() + " địa chỉ");

        return addresses;
    }

    public Address getDefaultAddress(String userId) {
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy khách hàng"));
        return addressRepository.findByCustomerAndIdAddressDefaultTrue(customer).orElse(null);
    }

    @Transactional
    public void addAddress(String userId, String street, String district, String city,
                           String phoneNumber, boolean isDefault) {
        System.out.println("🟢 [AddressService] addAddress() userId = " + userId);

        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy khách hàng để thêm địa chỉ"));

        if (isDefault) removeDefaultFlag(customer);

        Address address = new Address();
        address.setCustomer(customer);
        address.setStreet(street);
        address.setDistrict(district);
        address.setCity(city);
        address.setPhoneNumber(phoneNumber);
        address.setIdAddressDefault(true);

        addressRepository.save(address);
        System.out.println("✅ [AddressService] Đã thêm địa chỉ mới cho customerId = " + customer.getCustomerId());
    }

    /** 🔹 Cập nhật địa chỉ */
    @Transactional
    public void updateAddress(Integer addressId, String userId,
                              String street, String district, String city,
                              String phoneNumber, boolean isDefault) {
        System.out.println("🟢 [AddressService] updateAddress() addressId = " + addressId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy địa chỉ có ID = " + addressId));

        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy khách hàng"));

        // Kiểm tra quyền sửa địa chỉ
        if (!address.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("⚠️ Không thể chỉnh sửa địa chỉ của người khác");
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
        System.out.println("✅ [AddressService] Đã cập nhật địa chỉ ID = " + addressId);
    }

    /** 🔹 Đặt địa chỉ mặc định */
    @Transactional
    public void setDefaultAddress(String userId, Integer addressId) {
        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy khách hàng"));

        removeDefaultFlag(customer);

        Address target = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy địa chỉ"));

        if (target.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            target.setIdAddressDefault(true);
            addressRepository.save(target);
            System.out.println("✅ [AddressService] Đã đặt địa chỉ mặc định cho userId = " + userId);
        } else {
            throw new RuntimeException("⚠️ Không thể đặt mặc định cho địa chỉ của người khác");
        }
    }

    /** 🔹 Xóa địa chỉ */
    @Transactional
    public void deleteAddress(String userId, Integer addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy địa chỉ để xóa"));

        Customer customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy khách hàng"));

        if (!address.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("⚠️ Không thể xóa địa chỉ của người khác");
        }

        addressRepository.delete(address);
        System.out.println("🗑️ [AddressService] Đã xóa địa chỉ ID = " + addressId);
    }

    /** 🔹 Gỡ cờ mặc định khỏi các địa chỉ khác */
    @Transactional
    protected void removeDefaultFlag(Customer customer) {
        List<Address> addresses = addressRepository.findByCustomer(customer);
        for (Address addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIdAddressDefault())) {
                addr.getIdAddressDefault();
                addressRepository.save(addr);
            }
        }
        System.out.println("🔸 [AddressService] removeDefaultFlag() cho customerId = " + customer.getCustomerId());
    }
}
