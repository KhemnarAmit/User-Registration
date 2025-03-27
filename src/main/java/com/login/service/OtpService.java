package com.login.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;
    
    private String generatedOtp = null;
    private long otpGenerationTime = 0;

    // Generate a random 6-digit OTP and set the generation time
    public String generateOtp() {
        generatedOtp = String.format("%06d", new Random().nextInt(999999));
        otpGenerationTime = System.currentTimeMillis(); // Set the time of OTP generation
        return generatedOtp;
    }
    
    public String getGeneratedOtp() {
        return generatedOtp;
    }

    // Send OTP to user's email
    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Your OTP Code");
        helper.setText("Thank you for registration mr/ms. "+email+ "\n\nYour OTP code is: " + otp+"   \n\n  please be aware that this OTP is valid for only 5 minutes");

        mailSender.send(message);
    }

    // Verify OTP, checking if it is valid and not expired
    public boolean verifyOtp(String otp) {
        // Check if OTP is expired or not generated
        if (generatedOtp == null || System.currentTimeMillis() - otpGenerationTime > TimeUnit.MINUTES.toMillis(5)) {
            generatedOtp = null;  // Reset OTP if expired or not generated
            return false;
        }
        return generatedOtp.equals(otp);  // Check if provided OTP matches the generated OTP
    }
}
