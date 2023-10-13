package com.sbs.exam2.comment;

import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.answer.AnswerService;
import com.sbs.exam2.user.SiteUser;
import com.sbs.exam2.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

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
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(Principal principal,@PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s",comment.getAnswer().getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm commentForm, @PathVariable("id") Integer id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(@Valid CommentForm commentForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id) {
        if(bindingResult.hasErrors()) {
            return "answer_form";
        }
        Comment comment = this.commentService.getComment(id);
        System.out.println(comment.getAuthor().getUsername());
        System.out.println(principal.getName());
        if(!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/question/detail/%s#comment_%s",comment.getAnswer().getQuestion().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String commentVote(Principal principal,@PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.vote(comment, siteUser);
        return String.format("redirect:/question/detail/%s#comment_%s",comment.getAnswer().getQuestion().getId(), comment.getId());
    }
}
