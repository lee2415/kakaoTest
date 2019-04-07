package com.kakao.test.api.util;

import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.kakao.test.api.comparator.CountyLimitComparator;
import com.kakao.test.api.comparator.CountyRateComparator;
import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyLocation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SortUtil{

	/**
	 * 이차보전 비율 기준으로 내림차순 정렬하여 return하는 Method
	 * @param list
	 * @return
	 */
	public static List<County> sortAndRate(List<County> list){
		Collections.sort(list, new CountyRateComparator());
		return list;
	}

	/**
	 * 지원금액으로 내림차순 정렬 후 이차보전 비율로 추가 내림차순 정렬 후 전체를 return하는 Method
	 * @param list
	 * @return
	 */
	public static List<County> sortAndLimit(List<County> list){
		return sortAndLimit(list, list.size());
	}
	
	/**
	 * 지원금액으로 내림차순 정렬 후, 이차보전 비율로 추가 내림차순 정렬 후 입력받은 개수만큼 추출하여 return하는 Method
	 * @param list
	 * @param limitCount
	 * @return
	 */
	public static List<County> sortAndLimit(List<County> list, int limitCount){
		
		// 지원금액 기준으로 정렬 후
		Collections.sort(list, new CountyLimitComparator());
		
		// 이차보전 평균 비율 기준으로 추가 정렬 
		rateCompareList(list);
		
		for(County county : list) {
			log.debug("{}, {}", county.getLimit(), county.getRate());
		}

		// limit 개수에 맞춰 return 
		return list.subList(0, limitCount);
	}

	/**
	 * 지원금액 기준으로 정렬된 내용에서 지원금액이 같을 경우 같은 내에서 추가 정렬을 위한 Method
	 * @param list
	 */
	public static void rateCompareList(List<County> list) {
		int startIndex = 0;
		int endIndex = 0;
		Integer compareValue = -1;
		
		for(County county : list) {
			// 처음 진행할 경우 comapreValue에 초기값 설정 
			if(compareValue == -1) {
				compareValue = stringToValueInteger(county.getLimit());
				endIndex++;
			} else {
				// 비교 값과 현재 있는값이 같을 경우 endIndex를 추가하여 비교 영역을 넓힘.
				if(compareValue.compareTo(stringToValueInteger(county.getLimit())) == 0) {
					endIndex++;
				} else {
					// 값이 다를경우 정렬 진행 
					sortRate(list, startIndex, endIndex);
					startIndex = endIndex;
					endIndex++;
					compareValue = stringToValueInteger(county.getLimit());
				}
			}
		}
	}
	
	/**
	 * 입력 받은 start, end index내에서 list의 이차보전 비율 기준으로 내림차순 정렬 진행.
	 * @param list
	 * @param startIndex
	 * @param endIndex
	 */
	public static void sortRate(List<County> list, int startIndex, int endIndex) {
		for(int i=endIndex-1; i>startIndex; i--) {
			for(int j=startIndex;j<i;j++) {
				if(stringToRateFloat(list.get(i).getRate()).compareTo(stringToRateFloat(list.get(j).getRate())) == -1) {
					Collections.swap(list, i, j);
				}
			}
		}
	}
	
	/**
	 * 이차보전 비율 문자열로 된 값에 대해 수치화하여 비교가능한 숫자로 변환 
	 * 2% ~ 3% 일경우 3%기준으로 변환 진행함.
	 * @param str
	 * @return
	 */
	public static Float stringToRateFloat(String str) {
		if(str.contains("~")) {
			str = str.substring(str.indexOf("~"), str.length());
		}
		return Float.parseFloat(defaultString(str.replaceAll("[^0-9|.]", ""),"100"));
	}
	
	/**
	 * 지원금액에 대해 문자열로 된 값을 수치화하여 비교가능한 숫자로 변환
	 * 추천금액 내외의 경우 가장 작은 값으로 설정 
	 * @param str
	 * @return
	 */
	public static Integer stringToValueInteger(String str) {
		Integer resultValue = 0;
		if(str.contains("백만")){
			resultValue = Integer.parseInt(defaultString(str.replaceAll("\\D", ""),"0")) * 100;
		} else if(str.contains("억")) {
			resultValue =  Integer.parseInt(defaultString(str.replaceAll("\\D", ""),"0")) * 10000;
		}
		return resultValue;
	}
	
	public static String defaultString(String str, String defaultString) {
		if(StringUtils.isEmpty(str)) {
			return defaultString;
		} 
		return str;
	}
	
	/**
	 * 입력받은 좌표와 List상의 좌표와 비교하여 짧은 거리 순으로 정렬하는 Method
	 * @param x
	 * @param y
	 * @param list
	 */
	public static void sortDistance(double x, double y, List<County> list) {
		CountyLocation compare1 = null;
		CountyLocation compare2 = null;
		for(int i=list.size()-1;i>0;i--) {
			compare1 = list.get(i).getCountyCode().getLocation();
			for(int j=0;j<i;j++) {
				compare2 = list.get(j).getCountyCode().getLocation();
				if(distance(x, y, compare1.getX(), compare1.getY()).compareTo(distance(x, y, compare2.getX(), compare2.getY())) == -1){
					Collections.swap(list, i, j);
				}
			}
		}
	}
	
	/**
	 * 좌표끼리 거리를 추출하여 return 하는 Method
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static Double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(Math.abs(x2-x1), 2) + Math.pow(Math.abs(y2-y1), 2));
	}
	
	/**
	 * 입력한 지원금액보 같거나 작은 list의 index return하는 Method
	 * @param list
	 * @param limit
	 * @return
	 */
	public static int checkLimitIndex(List<County> list, Integer limit) {
		for(int i=0;i<list.size();i++) {
			if(limit.compareTo(stringToValueInteger(list.get(i).getLimit())) == -1){
				return i;
			}
		}
		return list.size();
	}
	
	/**
	 * 입력한 이차보전 비율보다 같거나 작은 list의 index return하는 Method
	 * @param list
	 * @param rate
	 * @return
	 */
	public static int checkRateIndex(List<County> list, Float rate) {
		for(int i=0;i<list.size();i++) {
			if(rate.compareTo(stringToRateFloat(list.get(i).getRate())) == -1){
				return i;
			}
		}
		return list.size();
	}
}
