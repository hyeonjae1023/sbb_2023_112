package com.sbs.exam2.question;

import com.sbs.exam2.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
    Page<Question> findByAuthor(SiteUser author, Pageable pageable);
    @Modifying
    @Query("update Question q set q.hit = q.hit + 1 where q.id = :id")
    int updateHit(@Param("id") int id);
}
