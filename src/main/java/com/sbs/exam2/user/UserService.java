package com.sbs.exam2.user;

import com.sbs.exam2.CommonUtil;
import com.sbs.exam2.DataNotFoundException;
import com.sbs.exam2.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CommonUtil commonUtil;


    public SiteUser create(String username, String email, String password) {
        SiteUser siteUser = new SiteUser();
        siteUser.setUsername(username);
        siteUser.setEmail(email);
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
        return siteUser;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if(siteUser.isPresent()){
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public boolean initiatePasswordReset(String email) {
        SiteUser user = userRepository.findByEmail(email).orElseThrow();

        if(user != null) {
            String newPassword = generatedRandomPassword();

            user.setPassword(newPassword);

            userRepository.save(user);

            emailService.sendPasswordResetEmail(user.getEmail(),newPassword);

            return true;
        }
        return false;
    }

    private String generatedRandomPassword() {
        // 임시 비밀번호 길이 설정
        int passwordLength = 6;

        // 임시 비밀번호 생성을 위한 문자열
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // 임시 비밀번호를 저장할 문자열
        StringBuilder password = new StringBuilder();

        // SecureRandom을 사용하여 임시 비밀번호 생성
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }

        return password.toString();
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        SiteUser user = userRepository.findByUsername(username).orElseThrow();

        if(user != null && passwordEncoder.matches(currentPassword,user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));

            userRepository.save(user);
            return true;
        }
        return false;
    }
}
