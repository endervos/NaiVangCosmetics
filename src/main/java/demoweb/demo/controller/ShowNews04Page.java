package demoweb.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sinh-vien-routine-ngon-bo-re")
public class ShowNews04Page {
    @GetMapping()
    public String showHomePage(Model model) {
        return "News/News04";
    }
}
