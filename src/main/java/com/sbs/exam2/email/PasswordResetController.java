package com.sbs.exam2.email;

import com.sbs.exam2.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordResetController {
    @Autowired
    private UserService userService;

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        boolean success = userService.initiatePasswordReset(email);

        if(success) {
            return ResponseEntity.ok("Password reset request has been sent to your email.");
        } else {
            return ResponseEntity.badRequest().body("User with the provided email not found.");
        }
    }
}
