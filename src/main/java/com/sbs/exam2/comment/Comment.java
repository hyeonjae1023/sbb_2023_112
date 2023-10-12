package com.sbs.exam2.comment;

import com.sbs.exam2.answer.Answer;
import com.sbs.exam2.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    private SiteUser author;
    @ManyToOne
    private Answer answer;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;
}
