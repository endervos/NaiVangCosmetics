package demoweb.demo.controller;

import demoweb.demo.entity.*;
import demoweb.demo.repository.InventoryRepository;
import demoweb.demo.repository.ItemRepository;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemImageService;
import demoweb.demo.service.ItemService;
import demoweb.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemImageService itemImageService;

    @Autowired
    private InventoryRepository inventoryRepository;

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

    @GetMapping("/{id:\\d+}")
    public String getItemById(@PathVariable("id") Integer id, Model model) {
        return itemService.getItemById(id)
                .map(item -> {
                    // Lấy số lượng tồn kho từ bảng Inventory
                    Integer stock = inventoryRepository.findById(id)
                            .map(Inventory::getQuantity)
                            .orElse(0);
                    model.addAttribute("stock", stock);


                    List<Review> reviews = reviewService.getByItemId(id);
                    for (Review review : reviews) {
                        if (review.getCustomer() == null) {
                            Customer c = new Customer();
                            User u = new User();
                            u.setFullname("Ẩn danh");
                            c.setUser(u);
                            review.setCustomer(c);
                        } else if (review.getCustomer().getUser() == null) {
                            User u = new User();
                            u.setFullname("Ẩn danh");
                            review.getCustomer().setUser(u);
                        }
                    }
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
        Category category = categoryService.findByCategoryId(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        List<Item> items = itemService.getItemsByCategoryIdWithFullData(id);
        model.addAttribute("category", category);
        model.addAttribute("items", items);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        model.addAttribute("currentCategory", category);
        return "Customer/Item";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Item> getItemDetail(@PathVariable Integer id) {
        Item item = itemService.getItemDetail(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createItem(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("color") String color,
            @RequestParam("ingredient") String ingredient,
            @RequestParam("price") Integer price,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Item item = itemService.createItem(name, description, color, ingredient, price, categoryId, image);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thêm sản phẩm thành công!");
            response.put("item", item);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateItem(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("color") String color,
            @RequestParam("ingredient") String ingredient,
            @RequestParam("price") Integer price,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Item item = itemService.updateItem(id, name, description, color, ingredient, price, categoryId, image);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật sản phẩm thành công!");
            response.put("item", item);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}