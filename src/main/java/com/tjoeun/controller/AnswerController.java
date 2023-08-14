package com.tjoeun.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.tjoeun.dto.AnswerFormDto;
import com.tjoeun.entity.Answer;
import com.tjoeun.entity.Question;
import com.tjoeun.entity.Users;
import com.tjoeun.service.AnswerService;
import com.tjoeun.service.QuestionService;
import com.tjoeun.service.UsersService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
	
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UsersService usersService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String createAnswer(@PathVariable("id") Long id, 
							   @Valid AnswerFormDto answerFormDto, 
							   BindingResult result, Model model,
							   Principal principal) {
		
		Question question = questionService.getQuestion(id);
		Users users = usersService.getUsers(principal.getName());
		
		if(result.hasErrors()) {
			model.addAttribute("question", question);
			return "question_detail";
		}
		
		Answer answer = answerService.createAnswer(question, answerFormDto.getContent(), users);
		
		return String.format("redirect:/question/detail/%s/#answer_%s", id, answer.getId());
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String answerModify(@PathVariable("id") Long id, AnswerFormDto answerFormDto, Principal principal) {
		
		Answer answer = answerService.getAnswer(id);
		
		if(!answer.getUsers().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
		}
		
		answerFormDto.setContent(answer.getContent());
		
		return "answer_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String answerModify(@PathVariable("id") Long id, 
							   @Valid AnswerFormDto answerFormDto, BindingResult result, 
							   Principal principal) {
		
		if(result.hasErrors()) {
			return "answer_form";
		}
		
		Answer answer = answerService.getAnswer(id);
		if(!answer.getUsers().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
		}
		
		answerService.modify(answer, answerFormDto.getContent());
		
		return String.format("redirect:/question/detail/%s/#answer_%s", answer.getQuestion().getId(), answer.getId());
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String answerDelete(@PathVariable("id") Long id,
							   Principal principal) {
		
		Answer answer = answerService.getAnswer(id);
		if(!answer.getUsers().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
		}
		
		answerService.delete(answer);
		
		return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
	}
	
	//	추천 저장
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String answerVote(@PathVariable("id") Long id, Principal principal) {
		
		Answer answer = answerService.getAnswer(id);
		Users users = usersService.getUsers(principal.getName());
		answerService.vote(answer, users);
		
		return String.format("redirect:/question/detail/%s/#answer_%s", answer.getQuestion().getId(), answer.getId());
		
	}
	
}
