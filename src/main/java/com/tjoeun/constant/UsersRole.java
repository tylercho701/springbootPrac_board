package com.tjoeun.constant;

import lombok.Getter;

@Getter
public enum UsersRole {
	USER("ROLE_USER"), ADMIN("ROLE_ADMIN");
	
	private String value;
	
	UsersRole(String value){
		this.value=value;
	}

}
