package com.kakao.test.common;

import java.util.HashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.kakao.test.common.exception.ErrorVo;

/**
 * 일관적인 return HttpStatus를 설정하기 위한 responseBase
 * 기본적으로 HttpStatus.OK로 return하고 코드로 에러 여부 체크하도록 설정 
 * @author ijeongseog
 *
 */
public class ResponseBase {
	
	public static <T> T ok() {
	    return ResponseBase.of(new ErrorVo(), MediaType.APPLICATION_JSON_UTF8_VALUE, HttpStatus.OK);
	}
	
	public static <T> T ok(Object object) {
	    return ResponseBase.of(object, MediaType.APPLICATION_JSON_UTF8_VALUE, HttpStatus.OK);
	}

	public static <T> T of(Object object) {
	    return ResponseBase.of(object, MediaType.APPLICATION_JSON_UTF8_VALUE, HttpStatus.OK);
	}

	public static <T> T of(Object object, HttpStatus httpStatus) {
		return ResponseBase.of(object, MediaType.APPLICATION_JSON_UTF8_VALUE, httpStatus);
	}

	public static <T> T of(Object object, String mediaType, HttpStatus httpStatus) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", mediaType);

		return (T) new ResponseEntity<Object>(object, headers, httpStatus);
	}

	public static <T> T error(ErrorVo errorVo) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

		HashMap<String, Object> error = new HashMap<String, Object>();
	    HashMap<String, Object> item = new HashMap<String, Object>();
	    item.put("code", errorVo.getCode());
	    item.put("message",errorVo.getMessage());

	    error.put("error", item);

		return (T) new ResponseEntity<HashMap>(error, headers, HttpStatus.OK);
	}

	public static <T> T error(Object object) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

		return (T) new ResponseEntity<Object>(object, headers, HttpStatus.OK);
	}
}
