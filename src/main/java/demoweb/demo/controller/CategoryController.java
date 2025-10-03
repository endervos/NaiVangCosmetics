package demoweb.demo.controller;

import demoweb.demo.entity.Category;
import demoweb.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // URL: /category/{id}/item
    @GetMapping("/category/{id}/item")
    public String showCategoryItems(@PathVariable("id") Integer id, Model model) {
        Category category = categoryService.findByCategoryId(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        // Truyền category hiện tại
        model.addAttribute("category", category);
        model.addAttribute("currentCategory", category); // 👈 Quan trọng cho sidebar
        model.addAttribute("categoryId", category.getCategoryId());
        // Truyền toàn bộ item thuộc category hiện tại
        model.addAttribute("items", category.getItems());
        // Truyền root categories để render sidebar
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());

        return "Customer/Item"; // file Thymeleaf để render danh sách sản phẩm
    }


}
