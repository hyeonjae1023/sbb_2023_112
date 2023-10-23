package com.sbs.exam2.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.answer.AnswerService;
import com.sbs.exam2.comment.Comment;
import com.sbs.exam2.comment.CommentService;
import com.sbs.exam2.model.KakaoProfile;
import com.sbs.exam2.model.OAuthToken;
import com.sbs.exam2.question.Question;
import com.sbs.exam2.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private AuthenticationManager authenticationManager;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "signup_form";
        }
        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2","password incorrect","패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/auth/kakao/callback")
    public String kakaoCallback(String code) throws JsonProcessingException { //Data를 리턴해주는 컨트롤러
        //POST방식으로 key=value 데이터를 요청(카카오 쪽으로)
        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","eaf798efc47254e12eba3a9a52b72302");
        params.add("redirect_uri","http://localhost:8090/user/auth/kakao/callback");
        params.add("code",code);

        //HttpHeader 와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
        new HttpEntity<>(params, headers);

        //Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        OAuthToken oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);

        System.out.println("카카오 엑세스 토큰: " + oAuthToken.getAccess_token());

        RestTemplate rt2 = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization","Bearer "+oAuthToken.getAccess_token());
        headers2.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader 와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        //Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper2 = new ObjectMapper();

        KakaoProfile kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);

        System.out.println("카카오 아이디(번호): " + kakaoProfile.getId());
        System.out.println("카카오 이메일: " + kakaoProfile.getId()+"@gmail.com");
        System.out.println("카카오 username: " + kakaoProfile.getId()+"__");
        UUID garbagePw = UUID.randomUUID();
        System.out.println("카카오 비번: "+garbagePw);

        SiteUser user = new SiteUser();

        userService.create(kakaoProfile.getId()+"__", kakaoProfile.getId()+"@gmail.com", garbagePw.toString());

        //로그인 처리
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        return "redirect:/";
    }
    @GetMapping("/changePassword")
    public String changePasswordForm(UserPasswordForm userPasswordForm) {
        return "password_form"; // 이는 changePassword.html 페이지를 나타냅니다.
    }
    @PostMapping("/changePassword")
    public String changePassword(@Valid UserPasswordForm userPasswordForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "password_form";
        }
        if(!userPasswordForm.getNewPassword().equals(userPasswordForm.getNewPassword2())) {
            bindingResult.rejectValue("password2","password incorrect","패스워드가 일치하지 않습니다.");
            return "password_form";
        }
            userService.changePassword(userPasswordForm.getUsername(),
                    userPasswordForm.getCurrentPassword(), userPasswordForm.getNewPassword());

        return "redirect:/";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(UserResetPasswordForm UserResetPasswordForm) {
        return "reset_password";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid UserResetPasswordForm UserResetPasswordForm, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "reset_password";
        }

        boolean checkExists = this.userService.getUserByEmailAndUsername(UserResetPasswordForm.getEmail(), UserResetPasswordForm.getUsername());

        if(!checkExists) {
            bindingResult.rejectValue("email","email incorrect","입력한 정보의 회원이 없습니다.");
            return "reset_password";
        }
            userService.initiatePasswordReset(UserResetPasswordForm.getEmail());

        return "redirect:/";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myprofile")
    public String myProfile(Model model,
                            @RequestParam(value="page",defaultValue = "0") int page,
                            Principal principal) {

        SiteUser user = userService.getUser(principal.getName());

        Page<Question> questions = this.questionService.getQuestionsByAuthor(page, user);
        Page<Answer> answers = this.answerService.getAnswersByAuthor(page, user);
        Page<Comment> comments = this.commentService.getCommentsByAuthor(page, user);

        model.addAttribute("user", user);
        model.addAttribute("questions", questions);
        model.addAttribute("answers", answers);
        model.addAttribute("comments", comments);
        return "myprofile";
    }
}
