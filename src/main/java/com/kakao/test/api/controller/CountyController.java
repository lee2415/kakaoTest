package com.kakao.test.api.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyCode;
import com.kakao.test.api.service.CountyService;
import com.kakao.test.api.util.NumberUtil;
import com.kakao.test.api.util.SortUtil;
import com.kakao.test.common.ResponseBase;
import com.kakao.test.common.exception.BizException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class CountyController {

	@Autowired
	private CountyService countyService;
	
	/**
	 * 데이터 파일에서 각 레코드를 데이터베이스에 저장하는 API
	 * csv에 있는 파일을 읽어와 DB에 저장한다. 
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/save")
	public ResponseEntity<Object> save() throws IOException{
		countyService.save();
		return ResponseBase.ok();
	}
	
	/**
	 * 지원하는 지자체 목록 검색 API
	 * 
	 * @return
	 */
	@GetMapping("/select")
	public ResponseEntity<Object> select() {
		return ResponseBase.ok(countyService.select());
	}
	
	/**
	 * 지원하는 지자체명을 입력 받아 해당 지자체의 지원정보를 출력하는 API
	 * @param countyCode
	 * @return
	 */
	@GetMapping("/select/region")
	public ResponseEntity<Object> selectRegion(@RequestBody CountyCode countyCode){
		if(StringUtils.isEmpty(countyCode.getRegion())){
			throw new BizException("ERROR.API", "검색을 위한 지자체명을 입력하세요.");
		}
		log.debug("region " +  countyCode.toString());
		return ResponseBase.ok(countyService.setlectRegion(countyCode.getRegion()));
	}
	
	/**
	 * 지원하는 지자체 정보 수정 기능 API
	 * region 기준으로 다른 데이터들을 수정한다.
	 * @param county
	 * @return
	 */
	@PutMapping("/update")
	public ResponseEntity<Object> update(@RequestBody County county){
		if(StringUtils.isEmpty(county.getCountyCode().getRegion())) {
			throw new BizException("ERROR.API", "수정하려는 지자체명은 필수 입니다.");
		}
		log.debug("input data : " + county.toString());
		
		return ResponseBase.ok(countyService.update(county));
	}
	
	/**
	 * 지원한도 컬럼에서 지원금액으로 내림차순 정렬(지원금액이 동일하면 이차보전 평균 비
		율이 적은 순서)하여 특정 개수만 출력하는 API 
	 * @param requestMap
	 * @return
	 */
	@GetMapping("/sort")
	public ResponseEntity<Object> selectSortAndLimit(@RequestBody Map<String, String> requestMap){
		if(requestMap.get("k") == null || !NumberUtil.isNumeric(requestMap.get("k"))) {
			throw new BizException("ERROR.API", "제한을 input이 없거나 잘못되었습니다.");
		}
		return ResponseBase.ok(countyService.selectSortAndLimit(Integer.parseInt(requestMap.get("k"))));
	}
	
	/**
	 * 이차보전 컬럼에서 보전 비율이 가장 작은 추천 기관명을 출력하는 API
	 * @return
	 */
	@GetMapping("/institute")
	public ResponseEntity<Object> selectInstitute(){
		return ResponseBase.ok(countyService.selectInstitute());
	}
	
	/**
	 * 특정 기사를 분석하여 본 사용자는 어떤 지자체에서 금융지원을 받는게 가장 좋을지 지 자체명을 추천하는 API
	 * @param requestMap
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/recommand")
	public ResponseEntity<Object> recommand(@RequestBody Map<String, String> requestMap) throws IOException{
		if(StringUtils.isEmpty(requestMap.get("input"))){
			throw new BizException("ERROR.API", "분석을 위한 신문기사 정보가 없습니다.");
		}
		return ResponseBase.ok(countyService.recommandRegion(requestMap.get("input")));
	}
		
}
