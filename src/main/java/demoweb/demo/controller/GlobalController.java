package demoweb.demo.controller;

import demoweb.demo.entity.Category;
import demoweb.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalController {

    private final CategoryService categoryService;

    @Autowired
    public GlobalController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ModelAttribute("rootCategories")
    public List<Category> populateCategories() {
        return categoryService.getRootCategoriesWithChildren();

    }


}
