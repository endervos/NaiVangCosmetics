package demoweb.demo.controller;

import demoweb.demo.entity.User;
import demoweb.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showAccountPage(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        model.addAttribute("user", user);
        return "Customer/AccountManage";
    }
}
