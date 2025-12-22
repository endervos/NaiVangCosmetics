package demoweb.demo.controller;

import demoweb.demo.dto.LoginRequest;
import demoweb.demo.dto.LoginResponse;
import demoweb.demo.entity.*;
import demoweb.demo.repository.AccountRepository;
import demoweb.demo.security.JwtTokenUtil;
import demoweb.demo.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FailedLoginService failedLoginService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    @GetMapping("/login-tthhn")
    public String showManagerLoginPage(Model model) {
        model.addAttribute("loginType", "manager");
        model.addAttribute("loginUrl", "/manager/login-tthhn");
        model.addAttribute("pageTitle", "Đăng nhập Manager");
        return "Manager/Login";
    }

    @PostMapping("/login-tthhn")
    @ResponseBody
    public ResponseEntity<?> managerLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            String validationError = validateLoginInput(loginRequest);
            if (validationError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LoginResponse(
                                false,
                                validationError,
                                null,
                                null
                        ));
            }
            Account account = accountRepository.findByUser_Email(loginRequest.getUsername())
                    .orElse(null);
            if (account != null) {
                if (failedLoginService.isAccountLocked(account.getAccountId())) {
                    long remainingMinutes = failedLoginService.getRemainingLockoutTime(account.getAccountId());
                    int failedAttempts = failedLoginService.getFailedAttemptsCount(account.getAccountId(), 5);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new LoginResponse(
                                    false,
                                    String.format("Tài khoản đã bị khóa do đăng nhập sai %d lần. Vui lòng thử lại sau %d phút.",
                                            failedAttempts, remainingMinutes),
                                    null,
                                    null
                            ));
                }
            }
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
            if (account == null) {
                account = accountRepository.findByUser_Email(loginRequest.getUsername())
                        .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
            }
            failedLoginService.recordSuccessfulLogin(account.getAccountId());
            String token = jwtTokenUtil.generateToken(userDetails, "ROLE_Manager");
            Session session = new Session();
            session.setAccount(account);
            session.setToken(token);
            session.setStartTime(LocalDateTime.now());
            session.setEndTime(LocalDateTime.now().plusHours(24));
            sessionService.save(session);
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
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse(
                            false,
                            "Tài khoản đã bị vô hiệu hóa",
                            null,
                            null
                    ));
        } catch (BadCredentialsException e) {
            Account account = accountRepository.findByUser_Email(loginRequest.getUsername())
                    .orElse(null);
            if (account != null) {
                failedLoginService.recordFailedLogin(account.getAccountId());
                int failedAttempts = failedLoginService.getFailedAttemptsCount(account.getAccountId(), 5);
                int remainingAttempts = 5 - failedAttempts;
                if (remainingAttempts > 0) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new LoginResponse(
                                    false,
                                    String.format("Email hoặc mật khẩu không đúng. Bạn còn %d lần thử.", remainingAttempts),
                                    null,
                                    null
                            ));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new LoginResponse(
                                    false,
                                    "Tài khoản đã bị khóa do đăng nhập sai quá 5 lần. Vui lòng thử lại sau 10 phút.",
                                    null,
                                    null
                            ));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            false,
                            "Email hoặc mật khẩu không đúng",
                            null,
                            null
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(
                            false,
                            "Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại sau.",
                            null,
                            null
                    ));
        }
    }

    private String validateLoginInput(LoginRequest loginRequest) {
        if (loginRequest == null) {
            return "Thông tin đăng nhập không được để trống";
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        if (username == null || username.trim().isEmpty()) {
            return "Email không được để trống";
        }
        if (!EMAIL_PATTERN.matcher(username.trim()).matches()) {
            return "Định dạng email không đúng";
        }
        if (password == null || password.isEmpty()) {
            return "Mật khẩu không được để trống";
        }
        if (password.length() < 15) {
            return "Mật khẩu phải có ít nhất 15 ký tự";
        }
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return "Mật khẩu phải có ít nhất 1 chữ hoa";
        }
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return "Mật khẩu phải có ít nhất 1 ký tự đặc biệt";
        }
        return null;
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
        List<Review> reviews = reviewService.getAllReviews();
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

    @GetMapping("/deal-management")
    public String showDealManagement(Model model) {
        return "Manager/DealManagement";
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
        Manager manager = managerService.getManagerByEmail(email);
        User user = manager.getUser();
        model.addAttribute("manager", user);
        model.addAttribute("managerId", manager.getManagerId());
        model.addAttribute("managerName", user.getFullname());
        return "Manager/Profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Manager manager = managerService.getManagerByEmail(email);
            User user = manager.getUser();
            user.setFullname(updatedUser.getFullname());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setBirthday(updatedUser.getBirthday());
            user.setGender(updatedUser.getGender());
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/manager/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/manager/profile";
        }
    }
}