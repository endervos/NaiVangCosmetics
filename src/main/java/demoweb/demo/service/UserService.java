package demoweb.demo.service;

import demoweb.demo.entity.User;
import demoweb.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Lấy User theo email (Optional) — dùng cho ReviewController
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Lấy User theo email, nếu không tồn tại ném exception
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Cập nhật User cơ bản
    public void updateUser(User updatedUser) {
        userRepository.findById(updatedUser.getUserId()).ifPresent(user -> {
            user.setFullname(updatedUser.getFullname());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setBirthday(updatedUser.getBirthday());
            user.setGender(updatedUser.getGender());
            userRepository.save(user);
        });
    }

    // Lưu User mới hoặc cập nhật
    public void save(User user) {
        userRepository.save(user);
    }
}
