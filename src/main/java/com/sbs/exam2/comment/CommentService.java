package com.sbs.exam2.comment;

import com.sbs.exam2.DataNotFoundException;
import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.answer.AnswerRepository;
import com.sbs.exam2.question.Question;
import com.sbs.exam2.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment create(Answer answer, String content, SiteUser siteUser) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(siteUser);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAnswer(answer);
        this.commentRepository.save(comment);
        return comment;
    }
    public Comment getComment(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }
    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }

    public void vote(Comment comment, SiteUser siteUser) {
        comment.getVoter().add(siteUser);
        this.commentRepository.save(comment);
    }
}
