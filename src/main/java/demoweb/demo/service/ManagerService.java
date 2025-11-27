package demoweb.demo.service;

import demoweb.demo.entity.Manager;
import demoweb.demo.entity.User;
import demoweb.demo.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private UserService userService;

    public Manager getManagerByEmail(String email) {
        User user = userService.findByEmail(email);
        return managerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
    }

    public void updateManager(Manager manager) {
        managerRepository.save(manager);
    }
}