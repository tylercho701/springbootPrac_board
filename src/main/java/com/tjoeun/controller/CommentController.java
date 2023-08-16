package com.tjoeun.controller;

import java.security.Principal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.tjoeun.dto.CommentFormDto;
import com.tjoeun.entity.Answer;
import com.tjoeun.entity.Comment;
import com.tjoeun.entity.Question;
import com.tjoeun.entity.Users;
import com.tjoeun.service.AnswerService;
import com.tjoeun.service.CommentService;
import com.tjoeun.service.QuestionService;
import com.tjoeun.service.UsersService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
	
	private final CommentService commentService;
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UsersService usersService;
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create/question/{id}")
	public String createComment(@PathVariable("id") Long id,
								CommentFormDto commentFormDto) {
		return "comment_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/question/{id}")
	public String createComment(@PathVariable("id") Long id,
								@Valid CommentFormDto commentFormDto,
								BindingResult result, Principal principal) {
		
		Question question = questionService.getQuestion(id);
		Users users = usersService.getUsers(principal.getName());
		
		if(result.hasErrors()) {
			return "comment_form";
		}
		
		Comment c = commentService.create(question, users, commentFormDto.getContent());
		
		return String.format("redirect:/question/detail/%s", c.getQuestionId());
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create/answer/{id}")
	public String createCommentA(@PathVariable("id") Long id,
								CommentFormDto commentFormDto) {
		return "comment_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/answer/{id}")
	public String createCommentA(@PathVariable("id") Long id,
								 @Valid CommentFormDto commentFormDto,
								 BindingResult result, Principal principal) {
		
		Answer answer = answerService.getAnswer(id);
		Users users = usersService.getUsers(principal.getName());
		
		if(result.hasErrors()) {
			return "comment_form";
		}
		
		Comment c = commentService.create(answer, users, commentFormDto.getContent());
		
		return String.format("redirect:/question/detail/%s/#answer_%s", c.getQuestionId(), c.getAnswer().getId());
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String modifyComment(@PathVariable("id") Long id,
								CommentFormDto commentFormDto,
								Principal principal) {
		
		Optional<Comment> comment = commentService.getComment(id);
		
		if(comment.isPresent()) {
			Comment c = comment.get();
			if(!c.getUsers().getUsername().equals(principal.getName())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한 없음");
			}
			commentFormDto.setContent(c.getContent());
		}
		return "comment_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modifyComment(@PathVariable("id") Long id,
								@Valid CommentFormDto commentFormDto,
								BindingResult result, Principal principal) {
		
		if(result.hasErrors()) {
			return "comment_form";
		}
		
		Optional<Comment> comment = commentService.getComment(id);
		
		if(comment.isPresent()) {
			
			Comment c = comment.get();
			
			if(!c.getUsers().getUsername().equals(principal.getName())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한 없음");
			}
			c = commentService.modify(c, commentFormDto.getContent());

			if(c.getAnswer() != null) {
				return String.format("redirect:/question/detail/%s/#answer_%s", c.getQuestionId(), c.getAnswer().getId());
			}
			
			return String.format("redirect:/question/detail/%s", c.getQuestionId());
			
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한 없음");
		}	
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String deleteComment(@PathVariable("id") Long id,
								Principal principal) {
		
		Optional<Comment> comment = commentService.getComment(id);
		
		if(comment.isPresent()) {
			Comment c = comment.get();
			if(!c.getUsers().getUsername().equals(principal.getName())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한 없음");
			}
			
			commentService.delete(c);
			
			return String.format("redirect:/question/detail/%s", c.getQuestionId());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "권한 없음");
		}	
	}
	
	
}
