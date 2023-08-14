package com.tjoeun.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tjoeun.entity.Answer;
import com.tjoeun.entity.Question;
import com.tjoeun.entity.Users;
import com.tjoeun.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	
	private final QuestionRepository questionRepository;
	
	//	질문 글 목록
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

	//	질문 글 수정
	public void modify(Question question, String subject, String content) {
		
		question.setSubject(subject);
		question.setContent(content);
		question.setModifyDate(LocalDateTime.now());
		questionRepository.save(question);
		
	}
	
	//	질문 글 삭제
	public void delete(Question question) {
		questionRepository.delete(question);
	}
	
	//	추천 저장
	public void vote(Question question, Users users) {
		question.getVoter().add(users);
		questionRepository.save(question);
	}
	
	//	검색	:	JPA에서 제공하는 specification 인터페이스 사용
	private Specification<Question> search(String keyword){
		
		Specification<Question> spec = new Specification<>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Predicate toPredicate(Root<Question> question, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				
				query.distinct(true);
				Join<Question, Users> qu = question.join("users", JoinType.LEFT);
				Join<Question, Answer> qa = question.join("answerList", JoinType.LEFT);
				Join<Answer, Users> au = qa.join("users", JoinType.LEFT);

				//	제목 / 내용 / 질문글쓴이 / 답글내용 / 답글글쓴이
				return criteriaBuilder.or(
					   criteriaBuilder.like(question.get("subject"), "%" + keyword + "%"),
					   criteriaBuilder.like(question.get("content"), "%" + keyword + "%"),
					   criteriaBuilder.like(qu.get("username"), "%" + keyword + "%"),
					   criteriaBuilder.like(qa.get("content"), "%" + keyword + "%"),
					   criteriaBuilder.like(au.get("username"), "%" + keyword + "%")
				);
			}
		};
		
		
		
		return spec;
	}
	
	//	QuestionRepository의 Page<Question> findAll(); 메소드 호출
	public Page<Question> getList(int page, String keyword){
		
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<Question> spec = search(keyword);
		
		return questionRepository.findAll(spec, pageable);
		
	}
	
	/*
	//	Specification<Question>를 사용하지 않는 경우
	public Page<Question> getList(int page, String keyword){
		
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		
		return questionRepository.findAllByKeyword(keyword, pageable);
		
	}
	*/
}
