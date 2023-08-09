package com.tjoeun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tjoeun.entity.Answer;
import com.tjoeun.entity.Question;
import com.tjoeun.repository.AnswerRepository;
import com.tjoeun.repository.QuestionRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class BoardAppTests {
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private AnswerRepository answerRepository;

	@Test
	@DisplayName("질문 테스트-1")
	void questionTest() {
		
		for(int i = 1; i<11; i++) {
		Question q1 = new Question();
		q1.setSubject("Spring Boot란" + i);
		q1.setContent("Spring Boot에 대해 알고 싶습니다." + i);
		q1.setCreateDate(LocalDateTime.now());
		questionRepository.save(q1);
		}
	}
	
	@Test
	@DisplayName("조회 테스트 - 2")
	void selectTest2() {
		
		Optional<Question> questionOne = questionRepository.findById((long)2);
		
		if(questionOne.isPresent()) {
			Question q1 = questionOne.get();			
			assertEquals("Querydsl ???", q1.getSubject());			
			
		}
	}
	
	@Test
	@DisplayName("조회 테스트 - 3")
	void selectTest3() {
		Question q1 = questionRepository.findBySubjectAndContent("Spring Boot ???", "Spring Boot 에 대해서 알고 싶습니다");
		assertEquals(1, q1.getId());
	}
	
	@Test
	@DisplayName("조회 테스트 - 4")
	void selectTest4() {
		List<Question> questionList = questionRepository.findBySubjectLike("Spr%%");
		
		// assertEquals(1, questionList.get(0).getId());		
		
		Question q1 = questionList.get(0);
		assertEquals(1, q1.getId());
	}
	
	@Test
	@DisplayName("수정 테스트 - 1")
	void updateTest1() {
		Optional<Question> q1 = questionRepository.findById((long)1);
		
		assertTrue(q1.isPresent());
			
		Question question = q1.get();
		question.setSubject("Spring Board ???");
		questionRepository.save(question);
		
	}
	
	@Test
	@DisplayName("삭제 테스트 - 1")
	void deleteTest1() {
		
		assertEquals(2, questionRepository.count());
		
		Optional<Question> q1 = questionRepository.findById((long)1);
		assertTrue(q1.isPresent());
		Question question = q1.get();
		questionRepository.delete(question);
		
		assertEquals(1, questionRepository.count());
		
	}
	
	@Test
	@DisplayName("답변 테스트 - 1")
	void answerTest1() {
		
		//	DB에서 2번 질문 글 가져오기
		Question q1 = questionRepository.findById((long)2)
										.orElseThrow(EntityNotFoundException::new);
		//	assertTrue(q1.isPresent());
		//	Question question = q1.get();
		Answer a1 = new Answer();
		a1.setContent("답변입니다.2");
		
		a1.setQuestion(q1);
		a1.setCreateDate(LocalDateTime.now());
		answerRepository.save(a1);
	}

	//	해당 답변 글에 대한 질문글의 번호가 2번이 맞는지 확인
	@Test
	@DisplayName("답변 테스트 - 2")
	void answerTest2() {
		
		Answer a1 = answerRepository.findById((long)1)
									.orElseThrow(EntityNotFoundException::new);
		
		assertEquals(2, a1.getQuestion().getId());
	}
	
	@Test
	@DisplayName("답변 테스트 - 3")
	void answerTest3() {
		
		Question q1 = questionRepository.findById((long)2)
										.orElseThrow(EntityNotFoundException::new);
		
		Answer a1 = answerRepository.findById((long)1)
				.orElseThrow(EntityNotFoundException::new);
		
		String answer = "답변입니다.2";
		
		assertEquals(answer, a1.getContent());
		
		assertEquals(a1.getQuestion().getId(), q1.getId());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}