package demoweb.demo.controller;

import demoweb.demo.entity.Item;
import demoweb.demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Hiển thị tất cả item
    @GetMapping
    public String showItemPage(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        return "Customer/Item"; // danh sách sản phẩm
    }

    // (tuỳ chọn) lấy lại tất cả items - có thể xoá nếu trùng
    @GetMapping("/all")
    public String getItems(Model model) {
        List<Item> items = itemService.getAllItems();
        System.out.println("DEBUG items = " + items.size());
        model.addAttribute("items", items);
        return "Customer/Item";
    }

    // Hiển thị chi tiết 1 item
    @GetMapping("/{id}")
    public String getItemById(@PathVariable("id") Integer id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    model.addAttribute("item", item);
                    return "Customer/ItemDetail"; // view chi tiết
                })
                .orElse("error/404");
    }
}
