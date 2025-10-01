package demoweb.demo.controller;

import demoweb.demo.entity.User;
import demoweb.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/favourite")
public class FavouriteListController {

    @Autowired
    private UserService userService;

    // GET /favourite
    @GetMapping
    public String favourite(Model model, Principal principal) {
        // lấy user từ email đăng nhập
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        // trả về đúng file template (ví dụ Customer/FavouriteList.html)
        return "Customer/FavouriteList";
    }
}
