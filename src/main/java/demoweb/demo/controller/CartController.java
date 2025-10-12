package demoweb.demo.controller;

import demoweb.demo.entity.*;
import demoweb.demo.repository.*;
import demoweb.demo.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final VoucherRepository voucherRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryRepository inventoryRepository;

    public CartController(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CartService cartService,
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            AddressRepository addressRepository,
            VoucherRepository voucherRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            InventoryRepository inventoryRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.voucherRepository = voucherRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // ====================== HIỂN THỊ GIỎ HÀNG ======================
    @GetMapping
    public String showCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            model.addAttribute("cartItems", List.of());
            model.addAttribute("cartCount", 0);
            return "Customer/Cart";
        }

        String email = userDetails.getUsername();
        String accountId = accountRepository.findByUser_Email(email)
                .map(Account::getAccountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account cho email: " + email));

        Optional<Cart> cartOpt = cartService.findCartByAccountId(accountId);
        List<CartItem> items = cartOpt.map(cart -> cartService.getCartItems(cart.getCartId())).orElse(List.of());

        model.addAttribute("cartItems", items);
        model.addAttribute("cartCount", items.size());
        return "Customer/Cart";
    }

    // ====================== THÊM SẢN PHẨM VÀO GIỎ ======================
    @PostMapping("/add")
    public String addToCart(@RequestParam("itemId") Integer itemId,
                            @RequestParam("quantity") Integer quantity,
                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String email = userDetails.getUsername();
        String accountId = accountRepository.findByUser_Email(email)
                .map(Account::getAccountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account cho email: " + email));

        Cart cart = cartService.findCartByAccountId(accountId).orElseGet(() -> {
            Cart c = new Cart();
            c.setAccountId(accountId);
            c.setCreatedAt(LocalDateTime.now());
            c.setUpdatedAt(LocalDateTime.now());
            return cartRepository.save(c);
        });

        Item itemEntity = cartService.findItemById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + itemId));

        CartItemId cartItemId = new CartItemId(cart.getCartId(), itemId);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem(cart, itemEntity, quantity);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return "redirect:/item/" + itemId;
    }

    // ====================== HIỂN THỊ TRANG THANH TOÁN ======================
    @GetMapping("/payment")
    public String showPaymentPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String email = userDetails.getUsername();
        String accountId = accountRepository.findByUser_Email(email)
                .map(Account::getAccountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account cho email: " + email));

        Optional<Cart> cartOpt = cartService.findCartByAccountId(accountId);
        if (cartOpt.isEmpty()) {
            model.addAttribute("cartItems", List.of());
            model.addAttribute("subtotal", 0);
            model.addAttribute("shippingCost", 0);
            model.addAttribute("discount", 0);
            model.addAttribute("totalAmount", 0);
            model.addAttribute("defaultAddress", null);
            model.addAttribute("addresses", List.of());
            return "Customer/Payment";
        }

        Cart cart = cartOpt.get();
        List<CartItem> cartItems = cartService.getCartItems(cart.getCartId());
        model.addAttribute("cartItems", cartItems);

        long subtotal = cartItems.stream()
                .mapToLong(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();

        int shipping = 0;
        int discount = 0;
        long total = subtotal + shipping - discount;

        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingCost", shipping);
        model.addAttribute("discount", discount);
        model.addAttribute("totalAmount", total);

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account với id: " + accountId));

        var customer = customerRepository.findByUser(account.getUser())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer cho user này"));

        List<Address> addresses = addressRepository.findByCustomer(customer);
        model.addAttribute("addresses", addresses);

        Optional<Address> defaultAddressOpt = addressRepository.findByCustomerAndIsDefaultTrue(customer);
        if (defaultAddressOpt.isPresent()) {
            Address addr = defaultAddressOpt.get();
            String fullAddress = String.format(
                    "%s | %s | %s, %s, %s",
                    addr.getCustomer().getUser().getFullname(),
                    addr.getPhoneNumber(),
                    addr.getStreet(),
                    addr.getDistrict(),
                    addr.getCity()
            );
            model.addAttribute("defaultAddress", fullAddress);
        } else {
            model.addAttribute("defaultAddress", "Chưa có địa chỉ giao hàng mặc định");
        }

        return "Customer/Payment";
    }

    // ====================== XỬ LÝ THANH TOÁN ======================
    @PostMapping("/checkout")
    @ResponseBody
    public Map<String, Object> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> payload) {

        if (userDetails == null)
            throw new RuntimeException("Chưa đăng nhập!");

        final String email = userDetails.getUsername();

        var account = accountRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account cho email: " + email));

        var customer = customerRepository.findByUser(account.getUser())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer cho account này"));

        Integer addressId = null;
        Object addrObj = payload.get("addressId");
        if (addrObj != null) {
            try {
                addressId = Integer.parseInt(addrObj.toString());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Địa chỉ không hợp lệ: " + addrObj);
            }
        }

        final Integer finalAddressId = addressId;

        String paymentMethod = Optional.ofNullable(payload.get("paymentMethod"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .orElse("Cash");

        String platform = Optional.ofNullable(payload.get("platform"))
                .map(Object::toString)
                .orElse("");

        double totalAmount = Optional.ofNullable(payload.get("totalAmount"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .map(Double::parseDouble)
                .orElse(0.0);

        String voucherCode = Optional.ofNullable(payload.get("voucherCode"))
                .map(Object::toString)
                .orElse("");

        var cart = cartService.findCartByAccountId(account.getAccountId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));

        var cartItems = cartService.getCartItems(cart.getCartId());
        if (cartItems.isEmpty())
            throw new RuntimeException("Giỏ hàng trống!");

        Voucher voucher = null;
        if (!voucherCode.isBlank()) {
            voucher = voucherRepository.findByCode(voucherCode).orElse(null);
        }

        Order order = new Order();
        order.setCustomer(customer);

        if (finalAddressId != null) {
            Address addr = addressRepository.findById(finalAddressId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ ID=" + finalAddressId));
            order.setAddress(addr);
        }

        order.setVoucher(voucher);
        order.setTotal((long) totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setPlacedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        for (CartItem c : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setItem(c.getItem());
            oi.setQuantity(c.getQuantity());
            oi.setPreDiscountPrice(c.getItem().getPrice());
            oi.setTotalPriceCents(c.getItem().getPrice() * c.getQuantity());
            orderItemRepository.save(oi);

            var inventory = inventoryRepository.findById(c.getItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy inventory cho sản phẩm ID=" + c.getItem().getItemId()));
            inventory.setReserved(inventory.getReserved() + c.getQuantity());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod.equalsIgnoreCase("Transfer") ? "Transfer" : "Cash");
        payment.setPlatform(platform);
        payment.setStatus("INITIATED");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        cartItemRepository.deleteAll(cartItems);

        if (voucher != null) {
            voucher.setUsedCount(voucher.getUsedCount() + 1);
            voucherRepository.save(voucher);
        }

        return Map.of(
                "status", "success",
                "orderId", order.getOrderId(),
                "paymentMethod", payment.getPaymentMethod(),
                "platform", platform,
                "message", "Thanh toán thành công! Sản phẩm đã được giữ chỗ trong kho."
        );
    }
}
