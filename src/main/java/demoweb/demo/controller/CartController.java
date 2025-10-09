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

    public CartController(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CartService cartService,
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            AddressRepository addressRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

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
        CartItemId id = new CartItemId(cart.getCartId(), itemId);
        CartItem item = cartItemRepository.findById(id).orElse(new CartItem(id, 0));
        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepository.save(item);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return "redirect:/item/" + itemId;
    }

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
        Optional<Address> defaultAddressOpt = accountRepository.findById(accountId)
                .map(Account::getUser)
                .flatMap(user -> customerRepository.findByUser(user))
                .flatMap(customer -> addressRepository.findByCustomerAndIsDefaultTrue(customer));
        defaultAddressOpt.ifPresentOrElse(addr -> {
            String fullAddress = String.format(
                    "%s | %s | %s, %s, %s",
                    addr.getCustomer().getUser().getFullname(),
                    addr.getPhoneNumber(),
                    addr.getStreet(),
                    addr.getDistrict(),
                    addr.getCity()
            );
            model.addAttribute("defaultAddress", fullAddress);
        }, () -> model.addAttribute("defaultAddress", "Chưa có địa chỉ giao hàng mặc định"));
        return "Customer/Payment";
    }
}