package demoweb.demo.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("naivangcosmeticsnhom4@gmail.com", "Nai Vàng Cosmetics");
            helper.setTo(toEmail);
            helper.setSubject("Xác thực Email - Nai Vàng Cosmetics");

            helper.setText(
                    "<p>Xin chào,</p>" +
                            "<p>Mã xác thực của bạn là: <b style='color:#d63384; font-size:16px;'>" + verificationCode + "</b></p>" +
                            "<p>Mã này có hiệu lực trong <b>5 phút</b>. Vui lòng nhập mã trên trang xác thực để hoàn tất đăng ký.</p>" +
                            "<p>Trân trọng,<br><b>Nai Vàng Cosmetics</b></p>",
                    true
            );

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }
}