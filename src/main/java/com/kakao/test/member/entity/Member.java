package com.kakao.test.member.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Member {

	@Id
	private String userId;
	private String userPass;
	
	public Member() {
	}
	
	public Member(String userId, String userPass) {
		super();
		this.userId = userId;
		this.userPass = userPass;
	}
	
}
