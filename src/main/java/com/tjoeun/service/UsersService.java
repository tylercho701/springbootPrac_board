package com.tjoeun.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
//	import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tjoeun.entity.Users;
import com.tjoeun.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {
	
	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	//	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public Users createUsers(String username, String password, String email) {
		
		//	String encodedPassword = bCryptPasswordEncoder.encode(password);
		String encodedPassword = passwordEncoder.encode(password);
		
		Users users = new Users();
		users.setUsername(username);
		users.setPassword(encodedPassword);
		users.setEmail(email);
		
		usersRepository.save(users);
		
		return users;
	}
	
	public Users getUsers(String username) {
		Users users = usersRepository.findByusername(username)
									 .orElseThrow(() -> new EntityNotFoundException("해당 회원이 없습니다."));
		
		return users;
		
		/*
		if(users.isPresent()) {
			return users.get();
		} else {
			throw new EntityNotFoundException("해당 회원이 없습니다.");
		}
		*/
	}
}
