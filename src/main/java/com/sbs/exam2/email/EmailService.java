package com.sbs.exam2.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendPasswordResetEmail(String to, String newPassword) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(to);

        mailMessage.setSubject("Password Reset");

        mailMessage.setText("Your new password: " + newPassword);

        javaMailSender.send(mailMessage);
    }
}
