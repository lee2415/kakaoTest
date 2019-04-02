package com.kakao.test.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyCode;
import com.kakao.test.common.exception.BizException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectConvertUtil {
	
	private final static String[] resultFilter = {"region", "target", "usage", "limit", "rate", "institute", "mgmt", "reception"};
	
	public static List<County> convertMappingVo(List<CSVRecord> records) {
		List<County> dataList = new ArrayList<>();
		
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
	    	
	    	CountyCode countyCode = new CountyCode();
	    	countyCode.setRegion(record.get(1));
	    	countyCode.setRegionCode(county.getRegionCode());
	    	county.setCountyCode(countyCode);
	    	dataList.add(county);
		}
		return dataList;
    }
	
	public static List<Map<String,String>> convertResultTypeList(List<County> list) {
		List<Map<String,String>> resultList = new LinkedList<Map<String,String>>();
		for(County county : list) {
			resultList.add(convertResultTypeMap(county));
		}
		return resultList;
	}
	
	public static Map<String, String> convertResultTypeMap(Object obj) {
		Map<String, String> resultMap = new HashMap<>();
		
		Method[] methods = obj.getClass().getDeclaredMethods();
		
		String methodName = null;
		String fieldName = null;
		
		Arrays.sort(resultFilter);
		for(Method m : methods) {
			methodName = m.getName();
			if(methodName.startsWith("get")) {
				fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4, methodName.length());
				
				try {
					if(!(m.invoke(obj) instanceof String) && m.invoke(obj) != null) {
						resultMap.putAll(convertResultTypeMap(m.invoke(obj)));
					} else {
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
