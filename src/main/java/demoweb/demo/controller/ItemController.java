package demoweb.demo.controller;

import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.Review;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemService;
import demoweb.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @Autowired
    public ItemController(ItemService itemService, CategoryService categoryService, ReviewService reviewService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    /** ✅ Trang tất cả sản phẩm */
    @GetMapping
    public String showItemPage(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        return "Customer/Item";
    }

    @GetMapping("/{id:\\d+}")
    public String getItemById(@PathVariable("id") Integer id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    // ✅ Lấy danh sách review theo itemId
                    List<Review> reviews = reviewService.getByItemId(id);

                    // ✅ Thêm các dữ liệu cần hiển thị
                    model.addAttribute("item", item);
                    model.addAttribute("reviews", reviews);

                    // ✅ Lấy luôn rating tổng hợp (đề phòng nếu bạn chưa attach trong ItemService)
                    model.addAttribute("averageRating", reviewService.getAverageRating(id));
                    model.addAttribute("reviewCount", reviewService.getReviewCount(id));
                    model.addAttribute("ratingStars", reviewService.getRatingStars(reviewService.getAverageRating(id)));

                    // ✅ Lấy root categories cho header/menu
                    model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());

                    return "Customer/ItemDetail";
                })
                .orElse("error/404");
    }


    /** ✅ Lọc sản phẩm theo khoảng giá */
    @GetMapping("/filter")
    public String filterItems(@RequestParam("categoryId") Integer categoryId,
                              @RequestParam("range") String range, Model model) {

        Integer min = null, max = null;
        switch (range) {
            case "0-500" -> { min = 0; max = 500_000; }
            case "500-1000" -> { min = 500_000; max = 1_000_000; }
            case "1000+" -> min = 1_000_000;
        }

        List<Item> items = itemService.filterByPrice(categoryId, min, max);
        Category category = categoryService.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        model.addAttribute("items", items);
        model.addAttribute("category", category);
        model.addAttribute("selectedRange", range);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        model.addAttribute("currentCategory", category);
        return "Customer/Item";
    }

    /** ✅ Trang sản phẩm theo danh mục */
    @GetMapping("/category/{id}/item")
    public String showCategoryItems(@PathVariable("id") Integer id, Model model) {
        System.out.println("===> Loading category items for categoryId = " + id);

        Category category = categoryService.findByCategoryId(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));


        List<Item> items = itemService.getItemsByCategoryIdWithFullData(id);

        System.out.println("===> Total items found: " + items.size());

        model.addAttribute("category", category);
        model.addAttribute("items", items);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        model.addAttribute("currentCategory", category);
        return "Customer/Item";
    }

}
