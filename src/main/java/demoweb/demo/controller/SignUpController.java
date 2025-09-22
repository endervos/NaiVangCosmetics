package demoweb.demo.controller;

import demoweb.demo.dao.CustomerTypeRepository;
import demoweb.demo.dao.RoleRepository;
import demoweb.demo.entity.SignUpCustomer;
import demoweb.demo.service.AccountService;
import demoweb.demo.service.CustomerService;
import demoweb.demo.service.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {
    private final CustomerService customerService;
    private final AccountService accountService;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    public SignUpController(CustomerService customerService, AccountService accountService, RoleRepository roleRepository, EmailService emailService) {
        this.customerService = customerService;
        this.accountService = accountService;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    @PostMapping
    public String handleRegister(@Valid @ModelAttribute("registerCustomer")SignUpCustomer signUpCustomer,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        if (accountService.existsByAccountId(signUpCustomer.setPassword();)) {
            bindingResult.rejectValue("email", "error.registerCustomer", "Tài khoản email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "customer/register";
        }
        session.setAttribute("registerCustomer", registerCustomer);
        String verificationCode = VerificationCodeGenerator.generateVerificationCode();
        session.setAttribute("verificationCode", verificationCode);
        emailService.sendVerificationEmail(registerCustomer.getEmail(), verificationCode);
        return "redirect:/register/verifyEmail";
    }

    @GetMapping("/verifyEmail")
    public String showVerifyEmailPage(HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterCustomer registerCustomer = (RegisterCustomer) session.getAttribute("registerCustomer");
        if (registerCustomer == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên đã hết hạn. Vui lòng đăng ký lại.");
            return "redirect:/register";
        }
        return "customer/verifyEmail";
    }

    @PostMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("verificationCode") String code,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String sessionCode = (String) session.getAttribute("verificationCode");
        if (sessionCode != null && sessionCode.equals(code)) {
            RegisterCustomer registerCustomer = (RegisterCustomer) session.getAttribute("registerCustomer");
            if (registerCustomer == null) {
                redirectAttributes.addFlashAttribute("error", "Phiên xác thực đã hết hạn. Vui lòng đăng ký lại.");
                return "redirect:/register";
            }
            Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER");
            if (customerRole == null) {
                customerRole = new Role();
                customerRole.setRoleName("ROLE_CUSTOMER");
                roleRepository.save(customerRole);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Account account = new Account(registerCustomer.getEmail(),
                    encoder.encode(registerCustomer.getPassword()), customerRole.getRoleID());
            accountService.save(account);
            Customer customer = new Customer();
            customer.setEmail(registerCustomer.getEmail());
            customer.setSsn(registerCustomer.getSsn());
            customer.setFullName(registerCustomer.getFullName());
            customer.setPhoneNumber(registerCustomer.getPhoneNumber());
            customer.setDOB(registerCustomer.getDOB());
            customer.setGender(registerCustomer.getGender());
            CustomerType defaultType = customerTypeRepository.findByCustomerTypeName("Khách vãng lai");
            if (defaultType == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại khách hàng mặc định.");
                return "redirect:/register";
            }
            customer.setCustomerType(defaultType);
            customerService.save(customer);
            session.removeAttribute("verificationCode");
            session.removeAttribute("registerCustomer");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Mã xác thực không đúng. Vui lòng thử lại.");
            return "redirect:/register/verifyEmail";
        }
    }

    @GetMapping()
    public String showSignUpPage(Model model)
    {
        return "SignUp/SignUp";
    }
}