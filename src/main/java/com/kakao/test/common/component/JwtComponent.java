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
	
	public String makeJwtToken(Member member) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		
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
	
	public boolean checkJwtApi(String jwt) throws BizException {
		if(StringUtils.isEmpty(jwt)) {
			throw new BizException("ERROR.JWT","인증에 실패하였습니다.");
		}
		try {
			jwtParser(jwt);
			return true;
		} catch (ExpiredJwtException exception) {
			log.error("토큰 만료");
			throw new BizException("ERROR.JWT","토큰이 만료되었습니다.");
		} catch (JwtException exception) {
			log.error("토큰 변조");
			throw new BizException("ERROR.JWT","인증에 실패하였습니다.");
		}
	}
	
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
	
	private Claims jwtParser(String jwt) {
		Claims claims = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
				.parseClaimsJws(jwt).getBody();
		
		return claims;
	}
}
 