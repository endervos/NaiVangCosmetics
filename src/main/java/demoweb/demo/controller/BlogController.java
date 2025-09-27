package demoweb.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {
    @GetMapping()
    public String showBlogPage(Model model) {
        return "Customer/Blog";
    }

    @GetMapping("/nice-routine-cua-ty-ty-douyin")
    public String showNews01Page(Model model) {
        return "Customer/News01";
    }

    @GetMapping("/mua-sam-tet-ga-khong-lo-ve-gia")
    public String showNews02Page(Model model) {
        return "Customer/News02";
    }

    @GetMapping("/huong-dan-trang-diem-mua-tua-truong")
    public String showNews03Page(Model model) {
        return "Customer/News03";
    }

    @GetMapping("/sinh-vien-routine-ngon-bo-re")
    public String showNews04Page(Model model) {
        return "Customer/News04";
    }

    @GetMapping("/ve-sinh-dung-cu-make-up")
    public String showNews05Page(Model model) {
        return "Customer/News05";
    }

    @GetMapping("/layout-di-bien-khong-troi")
    public String showNews06Page(Model model) {
        return "Customer/News06";
    }
}
