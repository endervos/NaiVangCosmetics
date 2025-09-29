package demoweb.demo.controller;

import demoweb.demo.entity.Item;
import demoweb.demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String showItemPage(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        return "Customer/Item";
    }

}
