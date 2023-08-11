package com.tjoeun.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tjoeun.constant.UsersRole;
import com.tjoeun.entity.Users;
import com.tjoeun.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersSecurityService implements UserDetailsService {
	
	private final UsersRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		/*
		Users users = usersRepository.findByusername(username)
									.orElseThrow(EntityNotFoundException);
		*/
		
		Optional<Users> tmpUsers = usersRepository.findByusername(username);
		
		if(tmpUsers.isEmpty()) {
			throw new UsernameNotFoundException("아직 가입하지 않은 회원입니다.");
		}
		
		Users users = tmpUsers.get();
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		if("admin".equals(username)) {
			authorities.add(new SimpleGrantedAuthority(UsersRole.ADMIN.getValue()));
		} else {
			authorities.add(new SimpleGrantedAuthority(UsersRole.USER.getValue()));
		}
		
		UserDetails user = new User(users.getUsername(), users.getPassword(), authorities);
		
		return user;

	} 
	
	
}
