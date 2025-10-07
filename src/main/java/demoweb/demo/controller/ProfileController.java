package demoweb.demo.controller;

import demoweb.demo.entity.User;
import demoweb.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showProfilePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        model.addAttribute("user", user);

        return "Customer/Profile";
    }


    /** ✅ Xử lý cập nhật thông tin người dùng */
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        String email = userDetails.getUsername();

        // Lấy user hiện tại từ DB
        User existingUser = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Cập nhật các trường cho phép
        existingUser.setFullname(updatedUser.getFullname());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setBirthday(updatedUser.getBirthday());
        existingUser.setGender(updatedUser.getGender());

        // Lưu lại vào DB
        userService.save(existingUser);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }
}

