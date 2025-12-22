package demoweb.demo.controller;

import demoweb.demo.dto.LoginRequest;
import demoweb.demo.dto.LoginResponse;
import demoweb.demo.entity.Account;
import demoweb.demo.entity.Session;
import demoweb.demo.repository.AccountRepository;
import demoweb.demo.security.JwtTokenUtil;
import demoweb.demo.service.AccountService;
import demoweb.demo.service.FailedLoginService;
import demoweb.demo.service.SessionService;
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
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginType", "customer");
        model.addAttribute("loginUrl", "/login");
        model.addAttribute("pageTitle", "Đăng nhập");
        return "Customer/Login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> customerLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
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
            boolean hasCustomerRole = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_Customer"));
            if (!hasCustomerRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new LoginResponse(
                                false,
                                "Tài khoản này không có quyền đăng nhập vào trang Customer. Vui lòng sử dụng trang đăng nhập phù hợp với vai trò của bạn.",
                                null,
                                null
                        ));
            }
            if (account == null) {
                account = accountRepository.findByUser_Email(loginRequest.getUsername())
                        .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
            }
            failedLoginService.recordSuccessfulLogin(account.getAccountId());
            String token = jwtTokenUtil.generateToken(userDetails, "ROLE_Customer");
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
                    "Đăng nhập thành công",
                    token,
                    "/"
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
}