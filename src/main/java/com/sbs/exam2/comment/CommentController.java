package com.sbs.exam2.comment;

import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.answer.AnswerForm;
import com.sbs.exam2.answer.AnswerService;
import com.sbs.exam2.question.Question;
import com.sbs.exam2.user.SiteUser;
import com.sbs.exam2.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final AnswerService answerService;
    private final UserService userService;
    private final CommentService commentService;
   @PreAuthorize("isAuthenticated()")
   @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("answer", answer);
            return "question_detail";
        }
        Comment comment = this.commentService.create(answer, commentForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s",answer.getQuestion().getId(), answer.getId());
    }
//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/create/{id}")
//    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
//
//        Question question = this.questionService.getQuestion(id);
//        SiteUser siteUser = this.userService.getUser(principal.getName());
//        if(bindingResult.hasErrors()){
//            model.addAttribute("question",question);
//            return "question_detail";
//        }
//        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);
//        return String.format("redirect:/question/detail/%s#answer_%s",answer.getQuestion().getId(), answer.getId());
//    }
}
