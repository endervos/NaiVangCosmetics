package demoweb.demo.controller;

import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.ItemImage;
import demoweb.demo.entity.Review;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemImageService;
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

    @GetMapping
    public String showItemPage(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        return "Customer/Item";
    }

    @Autowired
    private ItemImageService itemImageService;
    @GetMapping("/{id:\\d+}")
    public String getItemById(@PathVariable("id") Integer id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    List<Review> reviews = reviewService.getByItemId(id);

                    Double averageRating = reviewService.getAverageRating(id);
                    Integer reviewCount = reviewService.getReviewCount(id);
                    String ratingStars = reviewService.getRatingStars(averageRating);

                    ItemImage primaryImage = itemImageService.getPrimaryImage(id);
                    List<ItemImage> subImages = itemImageService.getImagesByItemId(id);

                    model.addAttribute("item", item);
                    model.addAttribute("reviews", reviews);
                    model.addAttribute("averageRating", averageRating);
                    model.addAttribute("reviewCount", reviewCount);
                    model.addAttribute("ratingStars", ratingStars);
                    model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
                    model.addAttribute("primaryImage", primaryImage);
                    model.addAttribute("subImages", subImages);

                    System.out.println("========== ITEM DETAIL DEBUG ==========");
                    System.out.println("Item ID: " + id);
                    System.out.println("Item Name: " + item.getName());
                    System.out.println("averageRating = " + averageRating);
                    System.out.println("reviewCount = " + reviewCount);
                    System.out.println("Primary Image: " + (primaryImage != null ? primaryImage.getItemImageId() : "none"));
                    System.out.println("Sub Images: " + subImages.size());
                    System.out.println("=======================================");

                    return "Customer/ItemDetail";
                })
                .orElse("error/404");
    }



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
