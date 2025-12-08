package demoweb.demo.controller;

import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemService;
import demoweb.demo.security.EncryptionUtil;
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
    private final EncryptionUtil encryptionUtil;

    @Autowired
    public CategoryController(CategoryService categoryService,
                              ItemService itemService,
                              EncryptionUtil encryptionUtil) {
        this.categoryService = categoryService;
        this.itemService = itemService;
        this.encryptionUtil = encryptionUtil;
    }

    @GetMapping("/category/{encryptedId}/item")
    public String showCategoryItems(@PathVariable("encryptedId") String encryptedId, Model model) {
        try {
            String decryptedId = encryptionUtil.decrypt(encryptedId);
            Integer id = Integer.parseInt(decryptedId);
            Category category = categoryService.findByCategoryId(id)
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            List<Item> items = itemService.getItemsByCategoryIdWithFullData(category.getCategoryId());
            model.addAttribute("category", category);
            model.addAttribute("currentCategory", category);
            model.addAttribute("categoryId", category.getCategoryId());
            model.addAttribute("items", items);
            model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
            model.addAttribute("encryptionUtil", encryptionUtil);
            return "Customer/Item";
        } catch (Exception e) {
            throw new RuntimeException("ID không hợp lệ", e);
        }
    }
}