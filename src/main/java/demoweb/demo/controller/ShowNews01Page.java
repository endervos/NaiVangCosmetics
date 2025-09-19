package demoweb.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nice-routine-cua-ty-ty-douyin")
public class ShowNews01Page {
    @GetMapping()
    public String showHomePage(Model model) {
        return "News/News01";
    }
}
