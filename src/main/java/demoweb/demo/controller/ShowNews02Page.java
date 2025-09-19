package demoweb.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mua-sam-tet-ga-khong-lo-ve-gia")
public class ShowNews02Page {
    @GetMapping()
    public String showHomePage(Model model) {
        return "News/News02";
    }
}
