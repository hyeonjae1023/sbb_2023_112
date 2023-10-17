package com.sbs.exam2.question;

import com.sbs.exam2.DataNotFoundException;
import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.user.SiteUser;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;


    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); //중복제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT); // 질문 작성자 정보
                Join<Question, Answer> a = q.join("answers", JoinType.LEFT); // 질문의 답변 정보
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT); // 답변 작성자 정보
                return  cb.or(cb.like(q.get("subject"), "%" + kw + "%"), //제목
                        cb.like(q.get("content"), "%" + kw + "%"), //내용
                        cb.like(u1.get("username"), "%" + kw + "%"), //질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"), //답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%")); //답변 작성자
            }
        };
    }
    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
    }

    public Page<Question> getQuestionsByAuthor(int page, SiteUser author) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
       return this.questionRepository.findByAuthor(author, pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = questionRepository.findById(id);
        if(question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser user, String category) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setAuthor(user);
        question.setCreateDate(LocalDateTime.now());
        question.setCategory(category);
        this.questionRepository.save(question);
    }

    public void modify(Question question, String content, String subject, String category) {
        question.setContent(content);
        question.setSubject(subject);
        question.setModifyDate(LocalDateTime.now());
        question.setCategory(category);
        this.questionRepository.save(question);
    }

    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }
}
