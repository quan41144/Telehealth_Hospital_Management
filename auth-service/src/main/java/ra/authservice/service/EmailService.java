package ra.authservice.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}
