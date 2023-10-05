package com.sbs.exam2;

import com.sbs.exam2.question.Question;
import com.sbs.exam2.question.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class Exam2ApplicationTests {
	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void testJpa() {
		Question q1 = new Question();
		q1.setSubject("제목1");
		q1.setContent("내용1");
		q1.setCreateDate(LocalDateTime.now());
		questionRepository.save(q1);

		Question q2 = new Question();
		q2.setSubject("제목2");
		q2.setContent("내용2");
		q2.setCreateDate(LocalDateTime.now());
		questionRepository.save(q2);

		List<Question> all = this.questionRepository.findAll();
		assertEquals(2, all.size());

		Question q = all.get(0);
		assertEquals("제목1",q.getSubject());

	}

}
