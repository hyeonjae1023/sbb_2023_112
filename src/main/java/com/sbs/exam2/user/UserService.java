package com.sbs.exam2.user;

import com.sbs.exam2.CommonUtil;
import com.sbs.exam2.DataNotFoundException;
import com.sbs.exam2.TempPasswordMail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempPasswordMail tempPasswordMail;
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

    public void modifyPassword(String email) throws TempPasswordMail.EmailException {
        String tempPassword = commonUtil.createTempPassword();
        SiteUser user = userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("해당 이메일의 유저가 없습니다."));
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        tempPasswordMail.sendSimpleMessage(email, tempPassword);
    }
}
