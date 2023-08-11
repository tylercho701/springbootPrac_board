package com.tjoeun.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tjoeun.entity.Question;
import com.tjoeun.entity.Users;
import com.tjoeun.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	
	private final QuestionRepository questionRepository;
	
	public Page<Question> getList(int page){
		
		List<Sort.Order> sorts = new ArrayList<>();
		
		sorts.add(Sort.Order.desc("createDate"));
		
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Page<Question> questionPage = questionRepository.findAll(pageable);
									//questionRepository.findAll();

		return questionPage;
	}
	
	public Question getQuestion(Long id) {
		Question question = questionRepository.findById(id)
											  .orElseThrow(EntityNotFoundException::new);
		
		return question;
	}
	
	//	질문 글 DB에 저장
	public void saveQuestion(String subject, String content, Users users) {
		
		Question question = new Question();
		question.setSubject(subject);
		question.setContent(content);
		question.setUsers(users);
		question.setCreateDate(LocalDateTime.now());
		
		questionRepository.save(question);
		
		
	}
	
}
