package demoweb.demo.controller;

import demoweb.demo.dto.LoginRequest;
import demoweb.demo.dto.LoginResponse;
import demoweb.demo.entity.*;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

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

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @GetMapping("/login_tthhn")
    public String showAdminLoginPage(Model model) {
        model.addAttribute("loginType", "admin");
        model.addAttribute("loginUrl", "/admin/login_tthhn");
        model.addAttribute("pageTitle", "Đăng nhập Admin");
        return "Admin/Login";
    }

    @PostMapping("/login_tthhn")
    @ResponseBody
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean hasAdminRole = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_Admin"));
            if (!hasAdminRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new LoginResponse(
                                false,
                                "Tài khoản này không có quyền đăng nhập vào trang Admin. Vui lòng sử dụng trang đăng nhập phù hợp với vai trò của bạn.",
                                null,
                                null
                        ));
            }
            String token = jwtTokenUtil.generateToken(userDetails, "ROLE_Admin");
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);
            return ResponseEntity.ok(new LoginResponse(
                    true,
                    "Đăng nhập thành công với quyền Admin",
                    token,
                    "/admin/dashboard"
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
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "Admin/Dashboard";
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

    @GetMapping("/account-management")
    public String showAccountManagement(Model model) {
        return "Admin/AccountManagement";
    }

    @GetMapping("/api/accounts")
    @ResponseBody
    public ResponseEntity<?> getAllAccounts() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            List<Map<String, Object>> accountList = accounts.stream()
                    .map(account -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("accountId", account.getAccountId());
                        map.put("userId", account.getUser().getUserId());
                        map.put("fullname", account.getUser().getFullname());
                        map.put("email", account.getUser().getEmail());
                        map.put("birthday", account.getUser().getBirthday());
                        map.put("gender", account.getUser().getGender() != null ?
                                account.getUser().getGender().name() : null);
                        map.put("phoneNumber", account.getUser().getPhoneNumber());
                        map.put("role", account.getRole().getName());
                        map.put("isActive", account.getIsActive());
                        return map;
                    })
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(accountList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy danh sách tài khoản: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/api/accounts/{accountId}")
    @ResponseBody
    public ResponseEntity<?> updateAccount(
            @PathVariable String accountId,
            @RequestParam("fullname") String fullname,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam("role") String role
    ) {
        try {
            Account updatedAccount = accountService.updateAccount(
                    accountId, fullname, birthday, gender, phoneNumber, role
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật tài khoản thành công!");

            Map<String, Object> accountData = new HashMap<>();
            accountData.put("accountId", updatedAccount.getAccountId());
            accountData.put("fullname", updatedAccount.getUser().getFullname());
            accountData.put("email", updatedAccount.getUser().getEmail());
            accountData.put("role", updatedAccount.getRole().getName());

            response.put("account", accountData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/api/accounts/{accountId}")
    @ResponseBody
    public ResponseEntity<?> deleteAccount(@PathVariable String accountId) {
        try {
            accountService.deleteAccount(accountId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa tài khoản thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/product-management")
    public String showProductManagement(Model model) {
        List<Item> items = itemService.getAllItems();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        return "Admin/ProductManagement";
    }

    @GetMapping("/category-management")
    public String showCategoryManagement(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "Admin/CategoryManagement";
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
        return "Admin/ReviewManagement";
    }

    @GetMapping("/order-management")
    public String showOrderManagement(Model model) {
        return "Admin/OrderManagement";
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

    @GetMapping("/deal-management")
    public String showDealManagement(Model model) {
        return "Admin/DealManagement";
    }

    @GetMapping("/api/vouchers")
    @ResponseBody
    public ResponseEntity<?> getAllVouchers() {
        try {
            List<Voucher> vouchers = voucherService.getAllVouchers();
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy danh sách voucher: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/api/vouchers/{id}")
    @ResponseBody
    public ResponseEntity<?> getVoucherById(@PathVariable Integer id) {
        try {
            Voucher voucher = voucherService.getVoucherById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
            return ResponseEntity.ok(voucher);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/api/vouchers")
    @ResponseBody
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
        try {
            Voucher savedVoucher = voucherService.createVoucher(voucher);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo voucher thành công!");
            response.put("voucher", savedVoucher);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/vouchers/{id}")
    @ResponseBody
    public ResponseEntity<?> updateVoucher(@PathVariable Integer id, @RequestBody Voucher voucher) {
        try {
            Voucher updatedVoucher = voucherService.updateVoucher(id, voucher);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật voucher thành công!");
            response.put("voucher", updatedVoucher);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/api/vouchers/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteVoucher(@PathVariable Integer id) {
        try {
            voucherService.deleteVoucher(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa voucher thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        String email = authentication.getName();
        Admin admin = adminService.getAdminByEmail(email);
        User user = admin.getUser();
        model.addAttribute("admin", user);
        model.addAttribute("adminId", admin.getAdminId());
        model.addAttribute("adminName", user.getFullname());
        return "Admin/Profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Admin admin = adminService.getAdminByEmail(email);
            User user = admin.getUser();
            user.setFullname(updatedUser.getFullname());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setBirthday(updatedUser.getBirthday());
            user.setGender(updatedUser.getGender());
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/admin/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/profile";
        }
    }
}