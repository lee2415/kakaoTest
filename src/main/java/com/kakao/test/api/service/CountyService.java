package com.kakao.test.api.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kakao.test.api.dao.CountyDao;
import com.kakao.test.api.entity.County;
import com.kakao.test.api.util.CsvUtil;
import com.kakao.test.api.util.ObjectConvertUtil;
import com.kakao.test.api.util.SearchWordUtil;
import com.kakao.test.api.util.SortUtil;
import com.kakao.test.common.exception.BizException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CountyService {
	
	@Autowired
	private CountyDao countyDao;
	
	public int save() throws IOException {
		// csv에서 데이터를 읽어와 List<County> 형태로 만들어 DB에 저장  
		List<County> countyList = ObjectConvertUtil.convertMappingVo(CsvUtil.readCsv());
		int insertCount = countyDao.saveAll(countyList).size();
		log.debug("insertCount : {}", insertCount);
		
		return insertCount;
	}
	
	public List<Map<String,String>> select() {
		// 전체 리스트를 조회하여 결과에 맞게 Map으로 변경하여 return 
		return ObjectConvertUtil.convertResultTypeList(countyDao.findAll());
	}
	
	public Map<String,String> setlectRegion(String region) {
		// 지역 명으로 조회하여 결과에 맞게 Map으로 변경하여 return 
		County county = countyDao.findByCountyCodeRegion(region);
		
		if(county == null) {
			throw new BizException("ERROR.API", "검색 결과가 없습니다.");
		}
		return ObjectConvertUtil.convertResultTypeMap(countyDao.findByCountyCodeRegion(region));
	}
	
	public Map<String,String> update(County updateCounty) {
		County selectCounty = countyDao.findByCountyCodeRegion(updateCounty.getCountyCode().getRegion());
		if(selectCounty == null) {
			throw new BizException("ERROR.API", "수정하려는 지자체 정보가 없습니다.");
		}
		// 수정하려는 지자체 정보를 조회하여 수정한 데이터들을 복하여 저장한다.
		ObjectConvertUtil.copyToConutyNotNullData(selectCounty, updateCounty);
		
		// 수정한 정보로 저장 수행 
		return ObjectConvertUtil.convertResultTypeMap(countyDao.save(selectCounty));
	}
	
	public Map<String, List<String>> selectSortAndLimit(int limitCount){
		Map<String, List<String>> resultMap = new HashMap<>();
		
		// 전체를 조회하여 정렬과 limit 개수만큼 출력 제한 
		List<County> list = SortUtil.sortAndLimit(countyDao.findAll(), limitCount);
		
		// 결과값에서 지자체명만 가져와 return 
		List<String> resultList = new LinkedList<String>();
		for(County county: list) {
			resultList.add(county.getCountyCode().getRegion());
		}
		resultMap.put("regionList", resultList);
		
		return resultMap; 
	}
	
	public Map<String, String> selectInstitute() {
		Map<String, String> resultMap = new HashMap<>();
		// 이차보전 비율 기준으로 정렬 
		List<County> list = SortUtil.sortAndRate(countyDao.findAll());
		
		// 이차보전 비율이 가장 작은 값으로 return 
		resultMap.put("region", list.get(0).getInstitute());
		return resultMap;
	}
	
	public Map<String,String> recommandRegion(String input) throws IOException {
		// 분석을 위한 전체 공백 제거 
		input = input.replaceAll(" ", "");
		
		// 신문기사 정보를 전달하여 문자열 검색으로 필요한 내용 추출 
		Map<String, Object> resultMap = SearchWordUtil.searchWord(input);
		
		// 용도에 대한 정보 조회 
		List<String> findUsage = (List<String>) resultMap.get("usage");
		
		List<County> countyList = null;
		// 하나만 검색된 경우 해당 용도가 포함되어 있는 데이터만 조회 
		if(findUsage.size() != 2 && findUsage.size() != 0) {
			countyList = countyDao.findByUsageContaining(findUsage.get(0));
		} else {
			countyList = countyDao.findAll();
		}

		// 용도기준으로 조회한 데이터를 지원금액 / 이차보전 비율 기준으로 정렬  
		SortUtil.sortAndLimit(countyList);
		
		// 검색된 지원금액으로 해당 지원금액보다 큰 데이터 삭제 
		Integer limit = (Integer) resultMap.get("limit");
		
		// 검색된 결과가 없을 경우 -1
		if(limit != -1) {
			int checkLimitIndex = SortUtil.checkLimitIndex(countyList, limit);
			// 검색한 금액 보다 큰 데이터가 없을 경우 list size를 return 
			if(checkLimitIndex != countyList.size()) {
				countyList = countyList.subList(0, checkLimitIndex);
			}
		}
		SortUtil.sortAndRate(countyList);
		
		Float rate = (Float) resultMap.get("rate");
		if(rate != -1) {
			int checkRateIndex = SortUtil.checkRateIndex(countyList, rate);
			if(checkRateIndex != countyList.size()) {
				countyList = countyList.subList(0, checkRateIndex);
			}
		}
		
		// 지역검색이 되었을 경우에 정렬 
		if(resultMap.get("x") != null && resultMap.get("y") != null) {
			double x = (double) resultMap.get("x");
			double y = (double) resultMap.get("y");

			log.debug("정렬 전");
			for(County county : countyList) {
				log.debug("Region : {}, distance : {} ",county.getCountyCode().getRegion(), SortUtil.distance(x, y, county.getCountyCode().getLocation().getX(), county.getCountyCode().getLocation().getY()));
			}
			
			SortUtil.sortDistance(x, y, countyList);
			log.debug("정렬 후");
			for(County county : countyList) {
				log.debug("Region : {}, distance : {}", county.getCountyCode().getRegion(), SortUtil.distance(x, y, county.getCountyCode().getLocation().getX(), county.getCountyCode().getLocation().getY()));
			}
		}
		
		if(countyList.size() == 0) {
			throw new BizException("ERROR.API", "조건에 맞는 추천 지자체가 없습니다.");
		}
		
		Map<String, String> recommandMap = new HashMap<>();
		recommandMap.put("region", countyList.get(0).getRegionCode());
		recommandMap.put("usage", countyList.get(0).getUsage());
		recommandMap.put("limit", countyList.get(0).getLimit());
		recommandMap.put("rate", countyList.get(0).getRate());
		
		return recommandMap;
	}
	
	
}
