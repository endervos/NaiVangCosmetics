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
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showProfilePage(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy email của người dùng đang đăng nhập
        String email = userDetails.getUsername();

        // Lấy thông tin user từ database
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Đẩy toàn bộ user lên view
        model.addAttribute("user", user);

        return "Customer/Profile";
    }
}
