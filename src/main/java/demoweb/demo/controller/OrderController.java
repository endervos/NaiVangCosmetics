package demoweb.demo.controller;

import demoweb.demo.entity.*;
import demoweb.demo.repository.*;
import demoweb.demo.security.EncryptionUtil;
import demoweb.demo.service.CategoryService;
import demoweb.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final EncryptionUtil encryptionUtil;

    @Autowired
    public OrderController(OrderRepository orderRepository,
                           CustomerRepository customerRepository,
                           AccountRepository accountRepository,
                           PaymentRepository paymentRepository,
                           CategoryService categoryService,
                           OrderService orderService,
                           EncryptionUtil encryptionUtil) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.encryptionUtil = encryptionUtil;
    }

    @GetMapping("/orderManage")
    public String showOrderManagement(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String email = userDetails.getUsername();
        var account = accountRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));
        var customer = customerRepository.findByUser(account.getUser())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));
        model.addAttribute("user", account.getUser());
        model.addAttribute("customerId", customer.getCustomerId());
        model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
        model.addAttribute("encryptionUtil", encryptionUtil);
        return "Customer/OrderManage";
    }

    @GetMapping("/api/orders/{customerId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getOrders(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String status) {
        List<Map<String, Object>> orders;
        if (status != null && !status.equals("all")) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderService.getOrdersByStatus(customerId, orderStatus);
        } else {
            orders = orderService.getOrdersByCustomer(customerId);
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orderManage/detail/{encryptedId}")
    public String showOrderDetail(@PathVariable String encryptedId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            String decryptedId = encryptionUtil.decrypt(encryptedId);
            Integer orderId = Integer.parseInt(decryptedId);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            String email = userDetails.getUsername();
            var account = accountRepository.findByUser_Email(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));
            var customer = customerRepository.findByUser(account.getUser())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));

            if (!order.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                return "error/403";
            }
            order.getOrderItems().size();
            order.getOrderItems().forEach(item -> {
                if (item.getItem() != null) {
                    item.getItem().getName();
                }
            });
            long tamTinh = order.getOrderItems().stream()
                    .mapToLong(item -> (long) item.getPreDiscountPrice() * item.getQuantity())
                    .sum();
            int giamGia = 0;
            if (order.getVoucher() != null) {
                giamGia = (int) (tamTinh * order.getVoucher().getDiscountPercent() / 100);
            }
            int phiVanChuyen = 0;
            long thanhTien = tamTinh - giamGia + phiVanChuyen;
            Payment payment = paymentRepository.findByOrder_OrderId(orderId).orElse(null);
            model.addAttribute("order", order);
            model.addAttribute("payment", payment);
            model.addAttribute("tamTinh", tamTinh);
            model.addAttribute("giamGia", giamGia);
            model.addAttribute("phiVanChuyen", phiVanChuyen);
            model.addAttribute("thanhTien", thanhTien);
            model.addAttribute("rootCategories", categoryService.getRootCategoriesWithChildren());
            model.addAttribute("encryptionUtil", encryptionUtil);
            return "Customer/DetailOrder";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/404";
        }
    }

    @PostMapping("/api/orders/{encryptedId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable String encryptedId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        if (userDetails == null) {
            response.put("success", false);
            response.put("message", "Chưa đăng nhập");
            return ResponseEntity.status(401).body(response);
        }
        try {
            String decryptedId = encryptionUtil.decrypt(encryptedId);
            Integer orderId = Integer.parseInt(decryptedId);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            String email = userDetails.getUsername();
            var account = accountRepository.findByUser_Email(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));
            var customer = customerRepository.findByUser(account.getUser())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));
            if (!order.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                response.put("success", false);
                response.put("message", "Không có quyền hủy đơn hàng này");
                return ResponseEntity.status(403).body(response);
            }
            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
                response.put("success", false);
                response.put("message", "Không thể hủy đơn hàng ở trạng thái hiện tại");
                return ResponseEntity.badRequest().body(response);
            }
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            response.put("success", true);
            response.put("message", "Đã hủy đơn hàng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/encrypt/{id}")
    @ResponseBody
    public ResponseEntity<String> encryptId(@PathVariable Integer id) {
        try {
            String encrypted = encryptionUtil.encrypt(id.toString());
            return ResponseEntity.ok(encrypted);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(id.toString());
        }
    }
}