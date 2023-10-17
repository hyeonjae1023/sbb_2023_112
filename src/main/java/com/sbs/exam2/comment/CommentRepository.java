package com.sbs.exam2.comment;

import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.question.Question;
import com.sbs.exam2.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByAnswer(Answer answer, Pageable pageable);
    Page<Comment> findByAuthor(SiteUser author, Pageable pageable);
}
