package com.kakao.test.api.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;

import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyCode;
import com.kakao.test.api.entity.CountyLocation;
import com.kakao.test.common.exception.BizException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectConvertUtil {
	
	private final static String[] resultFilter = {"region", "target", "usage", "limit", "rate", "institute", "mgmt", "reception"};
	
	/**
	 * CSV에서 읽어온 데이터를 Entity구조에 맞게 변경하는 Method
	 * @param records
	 * @return
	 */
	public static List<County> convertMappingVo(List<CSVRecord> records) {
		List<County> dataList = new ArrayList<>();
		// CSV 파일에서 읽어온 records 데이터를 활용하여 Entity구조에 맞게 데이터 저
		for(CSVRecord record : records) {
			County county = new County();
	    	county.setId(record.get(0));
	    	county.setRegionCode("reg" + String.format("%4s", record.get(0)).replace(' ', '0'));
	    	county.setTarget(record.get(2));
	    	county.setUsage(record.get(3));
	    	county.setLimit(record.get(4));
	    	county.setRate(record.get(5));
	    	county.setInstitute(record.get(6));
	    	county.setMgmt(record.get(7));
	    	county.setReception(record.get(8));
	    	
	    	// 내부적으로 거리 계산을 위해, 데이터 생성시 임의로 좌표 랜덤값으로 생성하여 저장 
	    	CountyLocation countyLocation = new CountyLocation();
	    	countyLocation.setRegionCode(county.getRegionCode());
	    	countyLocation.setX((double)Math.random() * 5 + 34);
	    	countyLocation.setY((double)Math.random() * 5 + 124);

	    	CountyCode countyCode = new CountyCode();
	    	countyCode.setRegion(record.get(1));
	    	countyCode.setRegionCode(county.getRegionCode());
	    	countyCode.setLocation(countyLocation);
	    	
	    	county.setCountyCode(countyCode);
	    	dataList.add(county);
		}
		
		log.debug(dataList.get(0).toString());
		return dataList;
    }
	
	/**
	 * List로 되어 있는 County를 List형태의 Map으로 변경하는 Method
	 * @param list
	 * @return
	 */
	public static List<Map<String,String>> convertResultTypeList(List<County> list) {
		List<Map<String,String>> resultList = new LinkedList<Map<String,String>>();
		for(County county : list) {
			resultList.add(convertResultTypeMap(county));
		}
		return resultList;
	}
	
	/**
	 * Object의 데이터를 Map으로 return으로 변경하는 Method
	 * 설정해둔 resultFilter에 있는 항목만 map으로 만들어 return
	 * @param obj
	 * @return
	 */
	public static Map<String, String> convertResultTypeMap(Object obj) {
		Map<String, String> resultMap = new HashMap<>();
		
		if(obj == null) {
			throw new BizException("ERROR.COMMON", "변환을 위한 데이터가 없습니다.");
		}
		
		//ojbect의 method를 가져옴 
		Method[] methods = obj.getClass().getDeclaredMethods();
		
		String methodName = null;
		String fieldName = null;
		
		Arrays.sort(resultFilter);
		for(Method m : methods) {
			methodName = m.getName();
			// mehtod 명이 get으로 시작하는 케이스로 체크 
			if(methodName.startsWith("get")) {
				fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4, methodName.length());
				
				try {
					// mehtod 실행한 결과값이 String이 아닐 경우 다시 호출하여 결과값을 받아옴. 
					if(!(m.invoke(obj) instanceof String) && m.invoke(obj) != null) {
						resultMap.putAll(convertResultTypeMap(m.invoke(obj)));
					} else {
						// method 실행된 결과값이 String일 경우 resultFilter에 있는지 확인 후 있을 경우 해당 값을 Map으로 추가
						if(Arrays.binarySearch(resultFilter, fieldName) >= 0) {
							resultMap.put(fieldName, (String) m.invoke(obj));
						}
					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new BizException(e);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new BizException(e);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new BizException(e);
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	private static List<String> selectNullFieldName(Object obj){
		List<String> resultList = new ArrayList<>();
		Method[] methods = obj.getClass().getDeclaredMethods();
		
		String methodName = null;
		String fieldName = null;
		for(Method m : methods) {
			methodName = m.getName();
			if(methodName.startsWith("get")) {
				fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4, methodName.length());
				try {
					if(m.invoke(obj) == null) {
						resultList.add(fieldName);
					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return resultList;
	}
	
	public static County copyToConutyNotNullData(County original, County change) {
		List<String> filterField = selectNullFieldName(change);
		log.debug(filterField.toString());
		BeanUtils.copyProperties(change, original, filterField.toArray(new String[filterField.size()]));
		return original;
	}
}
