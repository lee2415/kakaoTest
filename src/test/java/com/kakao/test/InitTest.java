package com.kakao.test;

import org.junit.Before;

import com.kakao.test.member.entity.Member;

public class InitTest {
	
	Member member = new Member();
	
	String userId = "leel2415";
	String userPass = "12341234";
	
	@Before
	public void init() {
		member.setUserId(userId);
		member.setUserPass(userPass);
	}

}
