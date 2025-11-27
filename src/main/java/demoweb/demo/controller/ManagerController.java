package demoweb.demo.controller;

import demoweb.demo.dto.LoginRequest;
import demoweb.demo.dto.LoginResponse;
import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.Review;
import demoweb.demo.entity.Order;
import demoweb.demo.security.JwtTokenUtil;
import demoweb.demo.service.*;
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

    @Autowired
    private OrderService orderService;

    @Autowired
    private DashboardService dashboardService;

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
        Map<String, Object> stats = dashboardService.getDashboardStats();
        model.addAttribute("totalProducts", stats.get("totalProducts"));
        model.addAttribute("totalCustomers", stats.get("totalCustomers"));
        model.addAttribute("totalOrders", stats.get("totalOrders"));
        model.addAttribute("monthlyRevenue", stats.get("monthlyRevenue"));
        model.addAttribute("pageTitle", "Manager Dashboard");
        return "Manager/Dashboard";
    }

    @GetMapping("/api/dashboard/revenue-chart")
    @ResponseBody
    public ResponseEntity<?> getRevenueChart() {
        try {
            Map<String, Object> chartData = dashboardService.getRevenueChart();
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy dữ liệu biểu đồ: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/api/dashboard/category-chart")
    @ResponseBody
    public ResponseEntity<?> getCategoryChart() {
        try {
            Map<String, Object> chartData = dashboardService.getCategoryDistribution();
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy dữ liệu biểu đồ: " + e.getMessage()
            ));
        }
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

    @GetMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Map<String, Object>> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/api/orders/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getOrderDetail(@PathVariable Integer orderId) {
        try {
            Map<String, Object> orderDetail = orderService.getOrderDetailForManager(orderId);
            return ResponseEntity.ok(orderDetail);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/api/orders/{orderId}/status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam("status") String status
    ) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái đơn hàng thành công!");
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", order.getOrderId());
            orderMap.put("status", order.getStatus().name());
            response.put("order", orderMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}