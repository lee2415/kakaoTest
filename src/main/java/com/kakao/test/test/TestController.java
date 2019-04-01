package com.kakao.test.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakao.test.common.ResponseBase;

@RestController
@RequestMapping("/test")
public class TestController {

	@GetMapping("/test")
	public ResponseEntity<Object> test(){
		return ResponseBase.ok();
	}
}
