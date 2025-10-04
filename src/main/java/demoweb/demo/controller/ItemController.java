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

import java.util.ArrayList;
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
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        return "Customer/Item";
    }

    // Hiển thị chi tiết 1 item
    @GetMapping("/{id:\\d+}")
    public String getItemById(@PathVariable("id") Integer id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    model.addAttribute("item", item);
                    return "Customer/ItemDetail";
                })
                .orElse("error/404");
    }

    // Lọc sản phẩm theo khoảng giá
    @GetMapping("/filter")
    public String filterItems(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("range") String range,
            Model model
    ) {
        Integer min = null, max = null;

        switch (range) {
            case "0-500":
                min = 0;
                max = 500_000;
                break;
            case "500-1000":
                min = 500_000;
                max = 1_000_000;
                break;
            case "1000+":
                min = 1_000_000;
                break;
        }

        List<Item> items = itemService.filterByPrice(categoryId, min, max);
        Category category = categoryService.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        model.addAttribute("items", items);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("selectedRange", range);
        model.addAttribute("category", category);
        model.addAttribute("currentCategory", category);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());

        return "Customer/Item";
    }

    // Hiển thị sản phẩm theo category
    @GetMapping("/category/{id}/item")
    public String showCategoryItems(@PathVariable("id") Integer id, Model model) {
        Category category = categoryService.findByCategoryId(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        List<Category> breadcrumb = new ArrayList<>();
        Category temp = category;
        while (temp != null) {
            breadcrumb.add(0, temp); // thêm cha trước, con sau
            temp = temp.getParent();
        }
        model.addAttribute("breadcrumb", breadcrumb);
        model.addAttribute("category", category);
        model.addAttribute("items", category.getItems());
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        model.addAttribute("currentCategory", category);

        return "Customer/Item";
    }
}
