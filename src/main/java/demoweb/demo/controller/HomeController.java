package demoweb.demo.controller;

import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping()
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FlashSaleService flashSaleService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BestSellerService bestSellerService;

    @GetMapping()
    public String showHomePage(Model model) {
        List<Category> rootCategories = categoryService.getRootCategoriesWithChildren();
        model.addAttribute("rootCategories", rootCategories);
        List<Map<String, Object>> flashSaleItems = flashSaleService.getTop5FlashSaleItems();
        model.addAttribute("flashSaleItems", flashSaleItems);
        List<Item> latestItems = itemService.getTop5LatestItems();
        model.addAttribute("latestItems", latestItems);
        if (!rootCategories.isEmpty()) {
            for (Category category : rootCategories) {
                List<Map<String, Object>> bestsellers =
                        bestSellerService.getTop10BestsellersByCategory(category.getCategoryId());
                model.addAttribute("bestsellers_" + category.getSlug(), bestsellers);
            }
        }
        return "Customer/Home";
    }
}