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

import java.util.List;

@Controller
public class CategoryController {

    private final CategoryService categoryService;
    private final ItemService itemService;

    @Autowired
    public CategoryController(CategoryService categoryService, ItemService itemService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
    }

    @GetMapping("/category/{id}/item")
    public String showCategoryItems(@PathVariable("id") Integer id, Model model) {
        Category category = categoryService.findByCategoryId(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        List<Item> items = itemService.getItemsByCategoryIdWithFullData(category.getCategoryId());
        model.addAttribute("category", category);
        model.addAttribute("currentCategory", category);
        model.addAttribute("categoryId", category.getCategoryId());
        model.addAttribute("items", items);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        return "Customer/Item";
    }
}
