package com.sbs.exam2.user;

import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.answer.AnswerService;
import com.sbs.exam2.comment.Comment;
import com.sbs.exam2.comment.CommentService;
import com.sbs.exam2.question.Question;
import com.sbs.exam2.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

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
