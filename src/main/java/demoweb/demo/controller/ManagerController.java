package demoweb.demo.controller;

import demoweb.demo.dto.LoginRequest;
import demoweb.demo.dto.LoginResponse;
import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.Review;
import demoweb.demo.security.JwtTokenUtil;
import demoweb.demo.service.AccountService;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemService;
import demoweb.demo.service.ReviewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/login_tthhn")
    public String showManagerLoginPage(Model model) {
        model.addAttribute("loginType", "manager");
        model.addAttribute("loginUrl", "/manager/login_tthhn");
        model.addAttribute("pageTitle", "Đăng nhập Manager");
        return "Manager/Login";
    }

    @PostMapping("/login_tthhn")
    @ResponseBody
    public ResponseEntity<?> managerLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean hasManagerRole = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_Manager"));
            if (!hasManagerRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new LoginResponse(
                                false,
                                "Tài khoản này không có quyền đăng nhập vào trang Manager. Vui lòng sử dụng trang đăng nhập phù hợp với vai trò của bạn.",
                                null,
                                null
                        ));
            }
            String token = jwtTokenUtil.generateToken(userDetails, "ROLE_Manager");
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);
            return ResponseEntity.ok(new LoginResponse(
                    true,
                    "Đăng nhập thành công với quyền Manager",
                    token,
                    "/manager/dashboard"
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            false,
                            "Email hoặc mật khẩu không đúng",
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(
                            false,
                            "Có lỗi xảy ra: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/dashboard")
    public String showDashboardPage(Model model) {
        model.addAttribute("pageTitle", "Manager Dashboard");
        return "Manager/Dashboard";
    }

    @GetMapping("/product-management")
    public String showProductManagement(Model model) {
        List<Item> items = itemService.getAllItems();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        return "Manager/ProductManagement";
    }

    @GetMapping("/category-management")
    public String showCategoryManagement(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "Manager/CategoryManagement";
    }

    @GetMapping("/api/category/{id}")
    @ResponseBody
    public ResponseEntity<?> getCategoryDetail(@PathVariable Integer id) {
        try {
            Category category = categoryService.findByCategoryId(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            Map<String, Object> response = new HashMap<>();
            response.put("categoryId", category.getCategoryId());
            response.put("name", category.getName());
            response.put("slug", category.getSlug());
            response.put("parentId", category.getParent() != null ? category.getParent().getCategoryId() : null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/category")
    @ResponseBody
    public ResponseEntity<?> createCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "parentId", required = false) Integer parentId
    ) {
        try {
            String slug = createUniqueSlug(name, null);
            Category category = categoryService.createCategory(name, slug, parentId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thêm danh mục thành công!");
            response.put("category", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/category/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam(value = "parentId", required = false) Integer parentId
    ) {
        try {
            String slug = createUniqueSlug(name, id);
            Category category = categoryService.updateCategory(id, name, slug, parentId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật danh mục thành công!");
            response.put("category", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String createSlug(String name) {
        String slug = name.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-");
        return slug;
    }

    private String createUniqueSlug(String name, Integer excludeCategoryId) {
        String baseSlug = createSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (categoryService.findBySlug(slug).isPresent()) {
            Category existing = categoryService.findBySlug(slug).get();

            if (excludeCategoryId != null && existing.getCategoryId().equals(excludeCategoryId)) {
                break;
            }
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }

    @GetMapping("/review-management")
    public String showReviewManagement(Model model) {
        List<Review> reviews = reviewService.getAll();
        model.addAttribute("reviews", reviews);
        return "Manager/ReviewManagement";
    }

    @GetMapping("/order-management")
    public String showOrderManagement(Model model) {
        return "Manager/OrderManagement";
    }
}