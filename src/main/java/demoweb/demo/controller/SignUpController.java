package demoweb.demo.controller;

import demoweb.demo.entity.SignUpCustomer;
import demoweb.demo.service.CustomerService;
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
            Model model) {
        if (!dto.isPasswordMatch()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp");
        }
        if (result.hasErrors()) {
            return "Customer/SignUp";
        }
        try {
            var user = customerService.signUpCustomer(dto);
            String code = customerService.generateAndSendVerification(user);
            model.addAttribute("userId", user.getUserId());
            return "Customer/VerifyCode";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "Customer/SignUp";
        }
    }

    @GetMapping("/verify-code")
    public String showVerifyPage(@RequestParam("userId") String userId, Model model) {
        model.addAttribute("userId", userId);
        return "Customer/VerifyCode";
    }

    @PostMapping("/verify-code")
    public String processVerify(
            @RequestParam("userId") String userId,
            @RequestParam("code") String code,
            Model model) {

        boolean verified = customerService.verifyCode(userId, code);

        if (verified) {
            model.addAttribute("successMessage", "Xác thực thành công! Tài khoản của bạn đã được kích hoạt.");
            return "Customer/Login";
        } else {
            model.addAttribute("userId", userId);
            model.addAttribute("errorMessage", "Mã xác thực không hợp lệ hoặc đã hết hạn.");
            return "Customer/VerifyCode";
        }
    }

//    @PostMapping("/resend")
//    public String resendCode(@RequestParam("userId") String userId, Model model) {
//        var user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
//        customerService.generateAndSendVerification(user);
//
//        model.addAttribute("userId", userId);
//        model.addAttribute("successMessage", "Mã xác thực mới đã được gửi.");
//        return "Verify/Verify";
//    }
}