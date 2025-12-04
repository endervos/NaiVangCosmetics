package demoweb.demo.controller;

import demoweb.demo.entity.*;
import demoweb.demo.repository.InventoryRepository;
import demoweb.demo.repository.ItemRepository;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemImageService;
import demoweb.demo.service.ItemService;
import demoweb.demo.service.ReviewService;
import demoweb.demo.security.EncryptionUtil;
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
    private final EncryptionUtil encryptionUtil;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemImageService itemImageService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    public ItemController(ItemService itemService,
                          CategoryService categoryService,
                          ReviewService reviewService,
                          EncryptionUtil encryptionUtil) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.encryptionUtil = encryptionUtil;
    }

    @GetMapping
    public String showItemPage(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        model.addAttribute("encryptionUtil", encryptionUtil);
        return "Customer/Item";
    }

    @GetMapping("/{encryptedId}")
    public String getItemById(@PathVariable("encryptedId") String encryptedId, Model model) {
        try {
            String decryptedId = encryptionUtil.decrypt(encryptedId);
            Integer id = Integer.parseInt(decryptedId);
            return itemService.getItemById(id)
                    .map(item -> {
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
                        model.addAttribute("encryptionUtil", encryptionUtil);
                        return "Customer/ItemDetail";
                    })
                    .orElse("error/404");
        } catch (Exception e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchItems(@RequestParam(value = "q", required = false) String query, Model model) {
        List<Item> items;
        if (query == null || query.trim().isEmpty()) {
            items = itemService.getAllItems();
        } else {
            if (!query.matches("^[a-zA-Z0-9\\sàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ]+$")) {
                model.addAttribute("error", "Từ khóa tìm kiếm không hợp lệ. Vui lòng chỉ sử dụng chữ cái và số.");
                items = List.of();
            } else {
                items = itemService.searchByName(query.trim());
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("searchQuery", query != null ? query.trim() : "");
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        model.addAttribute("encryptionUtil", encryptionUtil);
        return "Customer/SearchItem";
    }

    @GetMapping("/filter")
    public String filterItems(@RequestParam("categoryId") String encryptedCategoryId,
                              @RequestParam("range") String range, Model model) {
        try {
            String decryptedId = encryptionUtil.decrypt(encryptedCategoryId);
            Integer categoryId = Integer.parseInt(decryptedId);
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
            model.addAttribute("encryptionUtil", encryptionUtil);
            return "Customer/Item";
        } catch (Exception e) {
            throw new RuntimeException("ID không hợp lệ", e);
        }
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