package demoweb.demo.entity;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class VerificationCode {
    private static final String DIGITS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private final String code;
    private final LocalDateTime expiryTime;

    private VerificationCode(String code, LocalDateTime expiryTime) {
        this.code = code;
        this.expiryTime = expiryTime;
    }

    public static VerificationCode generate() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            builder.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return new VerificationCode(builder.toString(), LocalDateTime.now().plusMinutes(5));
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}