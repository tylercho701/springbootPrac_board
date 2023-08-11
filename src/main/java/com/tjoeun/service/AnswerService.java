package com.tjoeun.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.tjoeun.entity.Answer;
import com.tjoeun.entity.Question;
import com.tjoeun.entity.Users;
import com.tjoeun.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {
	
	private final AnswerRepository answerRepository;
	
	public void createAnswer(Question question, String content, Users users) {
		
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setUsers(users);
		
		answerRepository.save(answer);
	}
	
}
