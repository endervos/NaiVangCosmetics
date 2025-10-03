package demoweb.demo.controller;

import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    @Autowired
    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    // Hiển thị tất cả item
    @GetMapping
    public String showItemPage(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        return "Customer/Item";
    }

    // Hiển thị chi tiết 1 item theo id (chỉ số nguyên)
    @GetMapping("/{id:\\d+}")
    public String getItemById(@PathVariable("id") Integer id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    model.addAttribute("item", item);
                    return "Customer/ItemDetail";
                })
                .orElse("error/404");
    }


    @GetMapping("/filter")
    public String filterItems(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("range") String range,
            Model model
    ) {
        Double min = null, max = null;

        switch (range) {
            case "0-500":
                min = 0.0;
                max = 500000.0;
                break;
            case "500-1000":
                min = 500000.0;
                max = 1000000.0;
                break;
            case "1000+":
                min = 1000000.0;
                break;
        }

        // Lọc sản phẩm
        List<Item> items = itemService.filterByPrice(categoryId, min, max);

        // Lấy lại category để hiển thị sidebar + breadcrumb
        var category = categoryService.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        model.addAttribute("items", items);
        model.addAttribute("category", category);  // 👈 rất quan trọng
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("selectedRange", range);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());

        return "Customer/Item"; // file thymeleaf
    }


}
