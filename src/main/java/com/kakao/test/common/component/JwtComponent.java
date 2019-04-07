package com.kakao.test.common.component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kakao.test.common.exception.BizException;
import com.kakao.test.member.entity.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtComponent {

	@Value("${kakaopay.jwt-key}")
	private String secretKey;
	
	@Value("${kakaopay.access-token-validity-seconeds}")
	private int accessTokenValiditySeconds;
	
	/**
	 * JWT Token을 생성하는 Method 
	 * member 객체의 정보를 토큰 내부에 넣어서 생성한다.
	 * @param member
	 * @return
	 */
	public String makeJwtToken(Member member) {
		
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		
		// 내부 정보로 userID와 apiAuth 값을 넣어 체크
		Map<String, Object> claimsMap = new HashMap<String, Object>();
		claimsMap.put("userId", member.getUserId());
		claimsMap.put("apiAuth", true);
		
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.setHeaderParam("alg", "HS256")
			.setClaims(claimsMap)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenValiditySeconds * 1000))
			.signWith(signatureAlgorithm, signingKey)
			.compact();
	}
	
	/**
	 * 로그인 시에 로그인 정보와 JWT Token을 체크하여 인증 체크하는 Method
	 * @param jwt
	 * @param member
	 * @return
	 */
	public boolean checkJwt(String jwt, Member member) {
		try {
			Claims claims = jwtParser(jwt);
			
			log.debug("expireTime : " + claims.getExpiration());
			log.debug("userId : " + claims.get("userId"));
			log.debug("apiAuth : " + claims.get("apiAuth"));
			
			if(member.getUserId().equals(claims.get("userId")) && (boolean) claims.get("apiAuth")) {
				log.debug("인증 성공");
				return true;
			} else {
				return false;
			}
		} catch (ExpiredJwtException exception) {
			log.debug("토큰 만료");
			return false;
		} catch (JwtException exception) {
			log.debug("토큰 변조");
			return false;
		}
	}
	
	/**
	 * API 호출시에 Token에 대해 정상적인 토큰인지 체크하는 Method
	 * @param jwt
	 * @return
	 * @throws BizException
	 */
	public boolean checkJwtApi(String jwt) throws BizException {
		if(StringUtils.isEmpty(jwt)) {
			throw new BizException("ERROR.JWT","인증에 실패하였습니다.");
		}
		try {
			Claims claims = jwtParser(jwt);
			
			// Token 생성시 넣었던 apiAuth값이 true인지 체크하여 성공 여부 실행 
			if((Boolean)claims.get("apiAuth") == true) {
				return true;
			}
			
			return false;
		} catch (ExpiredJwtException exception) {
			log.error("토큰 만료");
			throw new BizException("ERROR.JWT","토큰이 만료되었습니다.");
		} catch (JwtException exception) {
			log.error("토큰 변조");
			throw new BizException("ERROR.JWT","인증에 실패하였습니다.");
		}
	}
	
	/**
	 * Token 정보를 받아 Token을 다시 생성하는 Method
	 * @param jwt
	 * @return
	 */
	public String refreshToken(String jwt) {
		try {
			Claims claims = jwtParser(jwt);
			Member member = new Member();
			member.setUserId((String) claims.get("userId"));
			
			return makeJwtToken(member);
		} catch (ExpiredJwtException exception) {
			log.debug("토큰 만료");
			throw new BizException("ERROR.JWT","토큰이 만료되었습니다.");
		} catch (JwtException exception) {
			log.debug("토큰 변조");
			throw new BizException("ERROR.JWT","인증에 실패하였습니다.");
		}
	}
	
	/**
	 * JWT 내용을 parsing하는 Method
	 * @param jwt
	 * @return
	 */
	private Claims jwtParser(String jwt) {
		Claims claims = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
				.parseClaimsJws(jwt).getBody();
		
		return claims;
	}
}
 