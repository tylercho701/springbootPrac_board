package com.tjoeun.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
					   Model model) {
		
		//	질문 글 전체 조회
		Page<Question> questionPage = questionService.getList(page);
		model.addAttribute("paging", questionPage);
		
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
	
	
	
	
	
}
