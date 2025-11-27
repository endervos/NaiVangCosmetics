package demoweb.demo.controller;

import demoweb.demo.entity.*;
import demoweb.demo.service.OrderService;
import demoweb.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orderManage")
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public String showOrderPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + email));
        Integer customerId = user.getCustomer().getCustomerId();
        model.addAttribute("customerId", customerId);
        model.addAttribute("user", user);
        return "Customer/OrderManage";
    }

    @GetMapping("/detail/{orderId}")
    public String showOrderDetail(@PathVariable Integer orderId,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(orderId);
        Optional<Payment> paymentOpt = orderService.getPaymentByOrderId(orderId);
        Optional<Address> addressOpt = Optional.empty();
        Optional<Voucher> voucherOpt = Optional.empty();
        if (order.getAddress() != null) {
            addressOpt = orderService.getAddressByOrderId(order.getAddress().getAddressId());
        }
        if (order.getVoucher() != null) {
            voucherOpt = orderService.getVoucherById(order.getVoucher().getVoucherId());
        }
        long tamTinh = orderItems.stream().mapToLong(OrderItem::getTotalPriceCents).sum();
        long giamGia = voucherOpt.map(v -> tamTinh * v.getDiscountPercent() / 100).orElse(0L);
        long phiVanChuyen = 30000; // mặc định 30.000đ
        long thanhTien = tamTinh - giamGia + phiVanChuyen;
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("payment", paymentOpt.orElse(null));
        model.addAttribute("address", addressOpt.orElse(null));
        model.addAttribute("voucher", voucherOpt.orElse(null));
        model.addAttribute("tamTinh", tamTinh);
        model.addAttribute("giamGia", giamGia);
        model.addAttribute("phiVanChuyen", phiVanChuyen);
        model.addAttribute("thanhTien", thanhTien);
        return "Customer/DetailOrder";
    }
}
