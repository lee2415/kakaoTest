package com.kakao.test.member.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kakao.test.common.component.JwtComponent;
import com.kakao.test.common.exception.BizException;
import com.kakao.test.common.util.EncryptUtil;
import com.kakao.test.member.dao.MemberDao;
import com.kakao.test.member.entity.Member;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService {

	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private JwtComponent jwtComponent;
	
	public String signup(Member member) {
		
		// 패스워드 암호화하여 저장하기 위해 암호화 
		String encryptUserPass = EncryptUtil.encrypt(member.getUserPass());
		log.debug(encryptUserPass);
		
		// 암호화된 값으로 변경하여 저장 
		member.setUserPass(encryptUserPass);
		memberDao.save(member);
			
		return jwtComponent.makeJwtToken(member);
	}
	
	public String signin(Member member) {
		Member selectMember = memberDao.getOne(member.getUserId());
		log.debug(selectMember.toString());
		
		if(selectMember.getUserPass().equals(EncryptUtil.encrypt(member.getUserPass()))){
			log.debug("login success");
			return jwtComponent.makeJwtToken(member);
		} else {
			log.debug("login fail");
			throw new BizException("ERROR.MEMBER", "로그인에 실패하였습니다.");
		}
	}
	
	public String refresh(String token) {
		return jwtComponent.refreshToken(token);
	}
}
