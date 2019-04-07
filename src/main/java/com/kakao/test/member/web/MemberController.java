package com.kakao.test.member.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakao.test.common.ResponseBase;
import com.kakao.test.common.exception.BizException;
import com.kakao.test.common.exception.ErrorVo;
import com.kakao.test.member.entity.Member;
import com.kakao.test.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;

/*
 * API 인증을 위해 JWT(Json Web Token)를 이용해서 Token 기반 API 
 * 인증 기능을 개발하고 각 API 호출 시에 HTTP Header 에 발급받은 토큰을 가지고 호출하세요.
 * o signup 계정생성 API: 입력으로 ID, PW 받아 내부 DB 에 계정 저장하고 토큰 생성하여 출력
 * § 단, 패스워드는 인코딩하여 저장한다.
 * § 단, 토큰은 특정 secret 으로 서명하여 생성한다.
 * 
 * o signin 로그인 API: 입력으로 생성된 계정 (ID, PW)으로 로그인 요청하면 토큰을 발급한다.
 * 
 * o refresh 토큰 재발급 API: 기존에 발급받은 토큰을 Authorization 헤더에 
 * “Bearer Token”으로 입력 요청을 하면 토큰을 재발급한다.

 */

@RequestMapping("/member")
@RestController
@Slf4j
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Value("${kakaopay.jwt-header-name}")
	private String AUTH_HEADER;
	
	// 재발급 구분을 위한 헤더 값 
	private final String REFRESH_CHECK_HEADER = "Bearer";

	/**
	 * 회원 가입을 위한 API
	 * userId와 userPass를 입력 받아 회원 가입 처리 후 토큰을 발급한다. 
	 * @param userId, userPass
	 * @return 
	 */
	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@RequestBody Member member, HttpServletResponse response) {
		
		log.debug(member.toString());
		
		// 입력한 값에서 필수값 체크 진행 
		if(StringUtils.isEmpty(member.getUserId()) || StringUtils.isEmpty(member.getUserPass())) {
			throw new BizException("ERROR.MEMBER", "회원 가입을 위한 필수값이 부족합니다.");
		}
		String jwtToken = memberService.signup(member);
		
		response.setHeader(AUTH_HEADER, jwtToken);
		return ResponseBase.ok();
	}
	
	/**
	 * 로그인 처리를 위한 API 
	 * @param userId, userPass
	 * @return
	 */
	@PostMapping("/signin")
	public ResponseEntity<Object> signin(@RequestBody Member member, HttpServletResponse response) {
		
		log.debug(member.toString());
		
		if(StringUtils.isEmpty(member.getUserId()) || StringUtils.isEmpty(member.getUserPass())) {
			throw new BizException("ERROR.MEMBER", "로그인을 위한 필수값이 부족합니다.");
		}
		String jwtToken = memberService.signin(member);
		
		response.setHeader(AUTH_HEADER, jwtToken);
		return ResponseBase.ok();
	}
	
	/**
	 * Token 재발긍블 위한 API
	 * token의 유효기간이 만료되지 않은경우만 사용가능
	 * 만료된경우 재 로그인 필
	 * @param response
	 * @return
	 */
	@PostMapping("/refresh")
	public ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response){
		
		log.debug("Auth Header : " + request.getHeader(AUTH_HEADER));
		
		String token = request.getHeader(AUTH_HEADER);
		if(StringUtils.isEmpty(token)) {
			log.debug("token이 없습니다.");
			throw new BizException("ERROR.MEMBER", "인증을 위한 Token이 없습니다.");
		}
		
		// 재발행시에는 Bearer 정보가 있것으로 체크하여 return 
		if(token.indexOf(REFRESH_CHECK_HEADER) < 0) {
			log.debug("재발행용 token 형식이 아닙니다.");
			throw new BizException("ERROR.MEMBER", "재발행용 token 형식이 아닙니다.");
		}
		
		token = token.substring(token.indexOf(REFRESH_CHECK_HEADER) + REFRESH_CHECK_HEADER.length() + 1);
		log.debug(token);
		
		String refreshToken = memberService.refresh(token);
		response.setHeader(AUTH_HEADER, refreshToken);
		
		return ResponseBase.ok();
	}
	
	
}
