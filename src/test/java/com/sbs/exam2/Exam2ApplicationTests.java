package com.sbs.exam2;

import com.sbs.exam2.question.Question;
import com.sbs.exam2.question.QuestionRepository;
import com.sbs.exam2.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class Exam2ApplicationTests {
	@Autowired
	private QuestionService questionService;

	@Test
	void testJpa() {
		for(int i = 1; i<= 300; i++) {
			String subject = String.format("테스트 데이터입니다:[%03d]",i);
			String content = "내용무";
			this.questionService.create(subject, content);
		}
	}
}
