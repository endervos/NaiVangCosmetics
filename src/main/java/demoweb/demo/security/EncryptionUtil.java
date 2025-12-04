package demoweb.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    @Value("${encryption.secret.key}")
    private String secretKey;

    private static final String ALGORITHM = "AES";

    public String encrypt(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa", e);
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi giải mã", e);
        }
    }
}