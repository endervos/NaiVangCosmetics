package demoweb.demo.controller;

import demoweb.demo.entity.*;
import demoweb.demo.repository.AddressRepository;
import demoweb.demo.service.CustomerService;
import demoweb.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final CustomerService customerService;
    private final AddressRepository addressRepository;

    @Autowired
    public ProfileController(UserService userService,
                             CustomerService customerService,
                             AddressRepository addressRepository) {
        this.userService = userService;
        this.customerService = customerService;
        this.addressRepository = addressRepository;
    }

    /**  Hiển thị trang Profile */
    @GetMapping()
    public String showProfilePage(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Lấy user từ email đăng nhập
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // 2. Lấy customer theo user
        Customer customer = customerService.getCustomerByUser(user);

        // 3. Lấy danh sách địa chỉ từ customer
        List<Address> addresses = addressRepository.findByCustomerAndIsDeleted(customer, 0);

        // 4. Gửi dữ liệu sang FE
        model.addAttribute("user", user);
        model.addAttribute("customer", customer);
        model.addAttribute("addresses", addresses);

        return "Customer/Profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {

        // 1. Lấy email từ user đang đăng nhập
        String email = userDetails.getUsername();

        // 2. Lấy user trong DB
        User existingUser = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // 3. Cập nhật các trường từ form gửi lên
        existingUser.setFullname(updatedUser.getFullname());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setBirthday(updatedUser.getBirthday());

        // 4. Lưu user
        userService.save(existingUser);


        // ============================================
        // 🔥 5. XỬ LÝ ĐỊA CHỈ (LUÔN LUÔN CÓ DỮ LIỆU)
        // ============================================

        // Lấy customer ứng với user
        Customer customer = customerService.getCustomerByUser(existingUser);

        String[] cities = request.getParameterValues("city[]");
        String[] districts = request.getParameterValues("district[]");
        String[] streets = request.getParameterValues("street[]");
        String[] isDefault = request.getParameterValues("isDefault[]");
        String[] isDeleted = request.getParameterValues("isDeleted[]");

        List<Address> oldAddresses = addressRepository.findByCustomer(customer);

// Lặp theo index FE gửi lên
        for (int i = 0; i < cities.length; i++) {

            Address addr;

            if (i < oldAddresses.size()) {
                addr = oldAddresses.get(i);   // Lấy address cũ
            } else {
                addr = new Address();         // Nếu là address mới
                addr.setCustomer(customer);
            }

            addr.setCity(cities[i]);
            addr.setDistrict(districts[i]);
            addr.setStreet(streets[i]);
            addr.setIdAddressDefault("1".equals(isDefault[i]));
            addr.setIsDeleted("1".equals(isDeleted[i]) ? 1 : 0);
            addr.setPhoneNumber(customer.getUser().getPhoneNumber());


            addressRepository.save(addr);
        }

        // ============================================

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }


}
