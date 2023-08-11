package com.tjoeun.controller;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tjoeun.dto.UsersFormDto;
import com.tjoeun.service.UsersService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
	
	private final UsersService usersService;
	
	@GetMapping("/signup")
	public String signUp(UsersFormDto usersFormDto) {
		
		return "signup_form";
	}
	
	@PostMapping("/signup")
	public String signUp(@Valid UsersFormDto usersFormDto, BindingResult result) {
		
		if(!usersFormDto.getPassword1().equals(usersFormDto.getPassword2())) {
			result.rejectValue("password2", "passwordIncorrect", "비밀번호가 일치하지 않습니다.");
		}
		
		if(result.hasErrors()) {
			return "signup_form";
		}
		
		//	회원 중복 검사
		try {
		usersService.createUsers(usersFormDto.getUsername(), usersFormDto.getPassword1(), usersFormDto.getEmail());
		} catch(DataIntegrityViolationException e) {
			e.printStackTrace();
			result.reject("signupFailed", "이미 가입한 회원 아이디입니다.");
			return "signup_form";
		} catch(Exception e) {
			e.printStackTrace();
			result.reject("signupFailed", e.getMessage());
			return "signup_form";
		}
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login_form";
	}
	
	
}
