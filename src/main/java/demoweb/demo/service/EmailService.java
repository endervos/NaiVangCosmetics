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
            helper.setFrom("lacitahotelnhom7@gmail.com", "La CiTa Hotel");
            helper.setTo(toEmail);
            helper.setSubject("Xác thực Email - La CiTa Hotel");
            helper.setText(
                    "<p>Xin chào,</p>" +
                            "<p>Mã xác thực của bạn là: <b>" + verificationCode + "</b></p>" +
                            "<p>Vui lòng nhập mã này trên trang xác thực để tiếp tục.</p>" +
                            "<p>Trân trọng,<br>La CiTa Hotel</p>", true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }
}
