package com.tjoeun.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.tjoeun.entity.Question;
import com.tjoeun.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	
	private final QuestionRepository questionRepository;
	
	public List<Question> findAll(){
		List<Question> questionList = questionRepository.findAll();
		
		return questionList;
	}
	
	public Question getQuestion(Long id) {
		Question question = questionRepository.findById(id)
											  .orElseThrow(EntityNotFoundException::new);
		
		return question;
	}
	
	//	질문 글 DB에 저장
	public void saveQuestion(String subject, String content) {
		
		Question question = new Question();
		question.setSubject(subject);
		question.setContent(content);
		question.setCreateDate(LocalDateTime.now());
		
		questionRepository.save(question);
		
		
	}
	
}
