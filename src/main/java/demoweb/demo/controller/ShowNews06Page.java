package demoweb.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/layout-di-bien-khong-troi")
public class ShowNews06Page {
    @GetMapping()
    public String showHomePage(Model model) {
        return "News/News06";
    }
}
