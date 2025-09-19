package demoweb.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/huong-dan-trang-diem-mua-tua-truong")
public class ShowNews03Page {
    @GetMapping()
    public String showHomePage(Model model) {
        return "News/News03";
    }
}
