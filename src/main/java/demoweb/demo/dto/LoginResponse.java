package demoweb.demo.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String redirectUrl;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message, String token, String redirectUrl) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.redirectUrl = redirectUrl;
    }

    // Getter và Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}