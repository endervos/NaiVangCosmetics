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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final Map<String, VerificationCode> verificationCodes = new HashMap<>();

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

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER chưa tồn tại"));

        Account account = new Account();
        account.setAccountId(UUID.randomUUID().toString());
        account.setUser(user);
        account.setRole(customerRole);
        account.setIsActive(false);

        accountRepository.save(account);

        Customer customer = new Customer();
        customer.setUser(user);
        customerRepository.save(customer);

        return user;
    }

    public String generateAndSendVerification(User user) {
        VerificationCode vc = VerificationCode.generate();
        verificationCodes.put(user.getUserId(), vc);
        emailService.sendVerificationEmail(user.getEmail(), vc.getCode());
        return vc.getCode();
    }

    public boolean verifyCode(String userId, String code) {
        VerificationCode vc = verificationCodes.get(userId);

        if (vc != null && !vc.isExpired() && vc.getCode().equals(code)) {
            Account account = accountRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));
            account.setIsActive(true);
            accountRepository.save(account);

            verificationCodes.remove(userId);
            return true;
        }
        return false;
    }
}