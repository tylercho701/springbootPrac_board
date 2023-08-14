package com.tjoeun.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.tjoeun.dto.AnswerFormDto;
import com.tjoeun.dto.QuestionFormDto;
import com.tjoeun.entity.Question;
import com.tjoeun.entity.Users;
import com.tjoeun.service.QuestionService;
import com.tjoeun.service.UsersService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
	
	private final QuestionService questionService;
	private final UsersService usersService;
	
	@GetMapping("/list")
	public String list(@RequestParam(value="page", defaultValue="0") int page,
					   @RequestParam(value="keyword", defaultValue="") String keyword,
					   Model model) {
		
		//	질문 글 전체 조회
		Page<Question> paging = questionService.getList(page, keyword);
		
		model.addAttribute("paging", paging);
		model.addAttribute("keyword", keyword);
		
		return "question_list";

	}
	
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model,
						 AnswerFormDto answerFormDto) {
		
		Question question = questionService.getQuestion(id);
		
		model.addAttribute("question", question);
		
		return "question_detail";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String createQuestion(QuestionFormDto questionFormDto) {
		
		return "question_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String createQuestion(@Valid QuestionFormDto questionFormDto,
								 BindingResult result, Principal principal) {
		
		Users users = usersService.getUsers(principal.getName());
		
		if(result.hasErrors()) {
			return "question_form";
		}
		
		//	질문 DB에 저장
		questionService.saveQuestion(questionFormDto.getSubject(), questionFormDto.getContent(), users);
		
		return "redirect:/question/list";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String questionModify(QuestionFormDto questionFormDto, @PathVariable("id") Long id,
								 Principal principal) {
		
		Question question = questionService.getQuestion(id);
		if(!question.getUsers().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
		}
		
		questionFormDto.setSubject(question.getSubject());
		questionFormDto.setContent(question.getContent());
		
		return "question_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String questionModify(@Valid QuestionFormDto questionFormDto,
								 @PathVariable("id") Long id, Principal principal,
								 BindingResult result) {
		
		if(result.hasErrors()) {
			return "question_form";
		}
		
		Question question = questionService.getQuestion(id);
		if(!question.getUsers().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
		}
		
		questionService.modify(question, questionFormDto.getSubject(), questionFormDto.getContent());
		
		return String.format("redirect:/question/detail/%s", id);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String questionDelete(@PathVariable("id") Long id,
								 Principal principal) {
		
		Question question = questionService.getQuestion(id);
		if(!question.getUsers().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
		}
		
		questionService.delete(question);
		
		
		
		return "redirect:/";
	}
	
	//	추천 저장
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String questionVote(@PathVariable("id") Long id, Principal principal) {
		
		Question question = questionService.getQuestion(id);
		Users users = usersService.getUsers(principal.getName());
		questionService.vote(question, users);
		
		return String.format("redirect:/question/detail/%s", id);
		
	}
	
	
	
	
	
	
	
	
}