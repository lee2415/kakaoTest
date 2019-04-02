package com.kakao.test.api.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kakao.test.api.dao.CountyDao;
import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyCode;
import com.kakao.test.common.exception.BizException;
import com.kakao.test.common.util.CsvUtil;
import com.kakao.test.common.util.ObjectConvertUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CountyService {
	
	@Autowired
	private CountyDao countyDao;
	
	public int save() throws IOException {
		
		List<County> countyList = ObjectConvertUtil.convertMappingVo(CsvUtil.readCsv());
		int insertCount = countyDao.saveAll(countyList).size();
		
		return insertCount;
	}
	
	public List<Map<String,String>> select() {
		return ObjectConvertUtil.convertResultTypeList(countyDao.findAll());
	}
	
	public Map<String,String> setlectRegion(String region) {
		return ObjectConvertUtil.convertResultTypeMap(countyDao.findByCountyCodeRegion(region));
	}
	
	public Map<String,String> update(County updateCounty) {
		if(StringUtils.isEmpty(updateCounty.getCountyCode().getRegion())) {
			throw new BizException("ERROR.API", "수정하려는 지자체명은 필수 입니다.");
		}
		
		County selectCounty = countyDao.findByCountyCodeRegion(updateCounty.getCountyCode().getRegion());
		ObjectConvertUtil.copyToConutyNotNullData(selectCounty, updateCounty);
		
		return ObjectConvertUtil.convertResultTypeMap(countyDao.save(selectCounty));
	}
}
