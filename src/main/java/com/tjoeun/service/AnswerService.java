package com.tjoeun.service;

import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.tjoeun.entity.Answer;
import com.tjoeun.entity.Question;
import com.tjoeun.entity.Users;
import com.tjoeun.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {
	
	private final AnswerRepository answerRepository;
	
	public Answer createAnswer(Question question, String content, Users users) {
		
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setUsers(users);
		
		Answer a1 = answerRepository.save(answer);
		
		return a1;
	}
	
	//	답글 조회
	public Answer getAnswer(Long id) {
		
		Answer answer = answerRepository.findById(id)
										.orElseThrow(() -> new EntityNotFoundException("답글이 없습니다."));
									//	.orElseThrow(EntityNotFoundException::new);
		
		return answer;
	}
	
	//	답글 수정
	public void modify(Answer answer, String content) {
		
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		answerRepository.save(answer);
		
	}
	
	//	답글 삭제
	public void delete(Answer answer) {
		answerRepository.delete(answer);
	}
	
	//	추천 저장
	public void vote(Answer answer, Users users) {
		answer.getVoter().add(users);
		answerRepository.save(answer);
	}
	
}
