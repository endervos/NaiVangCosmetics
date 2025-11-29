package demoweb.demo.service;

import demoweb.demo.entity.*;
import demoweb.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    /// address
    public Customer getCustomerByUser(User user) {
        return customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer cho user này"));
    }

    private final Map<String, String> verificationCodes = new HashMap<>();

    public User signUpCustomer(SignUpCustomer dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setFullname(dto.getFullname());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setBirthday(dto.getBirthday());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        userRepository.save(user);
        Role customerRole = roleRepository.findByName("Customer")
                .orElseThrow(() -> new RuntimeException("Role Customer chưa tồn tại"));
        Account account = new Account();
        account.setAccountId(UUID.randomUUID().toString());
        account.setUser(user);
        account.setRole(customerRole);
        account.setIsActive(true);
        accountRepository.save(account);
        Customer customer = new Customer();
        customer.setUser(user);
        CustomerType defaultType = customerTypeRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("CustomerType id=3 chưa tồn tại"));
        customer.setCustomerType(defaultType);
        customerRepository.save(customer);
        return user;
    }

    public String generateVerificationCode(String email) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        verificationCodes.put(email, code);
        return code;
    }

    public void sendVerificationEmail(String email, String code) {
        emailService.sendVerificationEmail(email, code);
    }

    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        if (savedCode != null && savedCode.equals(code)) {
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }


}