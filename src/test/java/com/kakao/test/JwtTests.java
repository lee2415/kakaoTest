package com.kakao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakao.test.common.component.JwtComponent;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class JwtTests extends InitTest{
	
	@Autowired
	private JwtComponent jwtComponent;
	
	@Test
	public void tokenSuccess() {
		String jwt = jwtComponent.makeJwtToken(member);
		log.debug(jwt);
		assert jwtComponent.checkJwt(jwt, member);
	}
	
	@Test
	public void tokenFail() {
		String jwt = jwtComponent.makeJwtToken(member);

		jwt = jwt.substring(0,jwt.length()-1) + "a";
		assert !jwtComponent.checkJwt(jwt, member);
	}
	
	@Test
	public void tokenFail2() {
		String jwt = jwtComponent.makeJwtToken(member);
		
		log.debug(jwt);
		
		try {
			Thread.sleep(1000 * 11);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert !jwtComponent.checkJwt(jwt, member);
	}
}
