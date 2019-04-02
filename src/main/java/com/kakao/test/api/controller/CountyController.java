package com.kakao.test.api.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyCode;
import com.kakao.test.api.service.CountyService;
import com.kakao.test.common.ResponseBase;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class CountyController {

	@Autowired
	private CountyService countyService;
	
	/**
	• 데이터 파일에서 각 레코드를 데이터베이스에 저장하는 API 개발
	• 지원하는 지자체 목록 검색 API 개발
	• 지원하는 지자체명을 입력 받아 해당 지자체의 지원정보를 출력하는 API 개발
	• 지원하는지자체정보수정기능API개발
	• 지원한도 컬럼에서 지원금액으로 내림차순 정렬(지원금액이 동일하면 이차보전 평균 비
		율이 적은 순서)하여 특정 개수만 출력하는 API 개발 o 입력:출력개수K
		o 출력: K 개의 지자체명 (e.g. { 강릉시, 강원도, 거제시, 경기도, 경상남도 } )
	• 이차보전 컬럼에서 보전 비율이 가장 작은 추천 기관명을 출력하는 API 개발
	 * @throws IOException 
	**/
	
	@PostMapping("/save")
	public ResponseEntity<Object> save() throws IOException{
		countyService.save();
		return ResponseBase.ok();
	}
	
	@GetMapping("/select")
	public ResponseEntity<Object> select() {
		return ResponseBase.ok(countyService.select());
	}
	
	@GetMapping("/select/region")
	public ResponseEntity<Object> selectRegion(@RequestBody CountyCode countyCode){
		log.debug("region " +  countyCode.toString());
		
		return ResponseBase.ok(countyService.setlectRegion(countyCode.getRegion()));
	}
	
	@PutMapping("/update")
	public ResponseEntity<Object> update(@RequestBody County county){
		log.debug("input data : " + county.toString());
		
		return ResponseBase.ok(countyService.update(county));
	}
	
}
