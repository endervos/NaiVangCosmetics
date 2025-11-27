package demoweb.demo.controller;

import demoweb.demo.dto.LoginRequest;
import demoweb.demo.dto.LoginResponse;
import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.security.JwtTokenUtil;
import demoweb.demo.service.AccountService;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.ItemService;
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

import java.util.List;

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
}