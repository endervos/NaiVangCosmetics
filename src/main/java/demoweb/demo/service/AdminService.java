package demoweb.demo.service;

import demoweb.demo.entity.Admin;
import demoweb.demo.entity.User;
import demoweb.demo.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserService userService;

    public Admin getAdminByEmail(String email) {
        User user = userService.findByEmail(email);
        return adminRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public void updateAdmin(Admin admin) {
        adminRepository.save(admin);
    }
}