package demoweb.demo.controller;

import demoweb.demo.entity.SignUpCustomer;
import demoweb.demo.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String showSignUpPage(Model model) {
        model.addAttribute("signUpCustomer", new SignUpCustomer());
        return "Customer/SignUp";
    }

    @PostMapping
    public String processSignUp(
            @Valid @ModelAttribute("signUpCustomer") SignUpCustomer dto,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (!dto.isPasswordMatch()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp");
        }
        if (result.hasErrors()) {
            return "Customer/SignUp";
        }
        try {
            String code = customerService.generateVerificationCode(dto.getEmail());
            customerService.sendVerificationEmail(dto.getEmail(), code);
            session.setAttribute("pendingUser", dto);
            session.setAttribute("verificationCode", code);
            return "redirect:/sign-up/verify-code";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "Customer/SignUp";
        }
    }

    @GetMapping("/verify-code")
    public String showVerifyPage() {
        return "Customer/VerifyCode";
    }

    @PostMapping("/verify-code")
    public String processVerify(@RequestParam("code") String code, HttpSession session, Model model) {
        String savedCode = (String) session.getAttribute("verificationCode");
        SignUpCustomer dto = (SignUpCustomer) session.getAttribute("pendingUser");

        if (savedCode == null || dto == null) {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin đăng ký. Vui lòng đăng ký lại.");
            return "Customer/SignUp";
        }
        if (savedCode.equals(code)) {
            customerService.signUpCustomer(dto);
            session.removeAttribute("pendingUser");
            session.removeAttribute("verificationCode");
            return "redirect:/";
        } else {
            model.addAttribute("errorMessage", "Mã xác thực không hợp lệ hoặc đã hết hạn.");
            return "Customer/VerifyCode";
        }
    }

    @PostMapping("/resend-code")
    public String resendCode(HttpSession session, Model model) {
        SignUpCustomer dto = (SignUpCustomer) session.getAttribute("pendingUser");
        if (dto == null) {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin đăng ký. Vui lòng đăng ký lại.");
            return "Customer/SignUp";
        }
        try {
            String newCode = customerService.generateVerificationCode(dto.getEmail());
            customerService.sendVerificationEmail(dto.getEmail(), newCode);
            session.setAttribute("verificationCode", newCode);
            model.addAttribute("successMessage", "Mã xác thực mới đã được gửi lại email của bạn.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không thể gửi lại mã: " + e.getMessage());
        }
        return "Customer/VerifyCode";
    }
}