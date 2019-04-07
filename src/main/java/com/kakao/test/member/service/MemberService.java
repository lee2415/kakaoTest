package com.kakao.test.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kakao.test.common.component.JwtComponent;
import com.kakao.test.common.exception.BizException;
import com.kakao.test.member.dao.MemberDao;
import com.kakao.test.member.entity.Member;
import com.kakao.test.member.util.EncryptUtil;

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
			
		// 가입된 정보 기준으로 JWT Token 발행하여 return 
		return jwtComponent.makeJwtToken(member);
	}
	
	public String signin(Member member) {
		Member selectMember = memberDao.findByUserId(member.getUserId());
		if(selectMember == null) {
			throw new BizException("ERROR.MEMBER", "가입되지 않은 회원입니다.");
		}
		log.debug(selectMember.toString());
		
		// 암호화된 값과 입력받은 값을 암호화하여 비교하여 로그인 여부 판단 
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
