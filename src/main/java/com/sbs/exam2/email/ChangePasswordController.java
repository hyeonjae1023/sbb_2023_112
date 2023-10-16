package com.sbs.exam2.email;

import com.sbs.exam2.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangePasswordController {
    @Autowired
    private UserService userService;

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam String username, @RequestParam String currentPassword, @RequestParam String newPassword) {
        boolean success = userService.changePassword(username, currentPassword, newPassword);

        if(success) {
            return ResponseEntity.ok("Password has been changed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Password change failed. Please check your current password.");
        }
    }
}
