package com.kakao.test.api.util;

import java.util.ArrayList;
import java.util.List;

public class KmpUtil {
	/**
	 * 검색의 속도를 위한 PI값을 계산하는 Method
	 * @param pattern
	 * @return
	 */
	private static int[] getPI(String pattern) {
		int m = pattern.length();
		int j = 0;
		int[] pi = new int[m];
		
		for(int i=1;i<m;i++) {
			while(j>0 && pattern.charAt(i) != pattern.charAt(j)) {
				j = pi[j-1];
			}
			if(pattern.charAt(i) == pattern.charAt(j)) {
				pi[i] = ++j;
			}
		}
		return pi;
	}
	
	/** 
	 * 문장과 찾으려 하는 문자열을 입력 받아 해당 문자열이 있는 index를 List로 return하는 Method
	 * @param searchData
	 * @param pattern
	 * @return
	 */
	public static List<Integer> search(String searchData, String pattern){
		List<Integer> result = new ArrayList<>();
		// 찾으려 하는 문자열의 PI값을 계산 
		int[] pi = getPI(pattern);
		int n = searchData.length();
		int m = pattern.length();
		int j = 0;
		
		for(int i=0;i<n;i++) {
			
			while(j > 0 && searchData.charAt(i) != pattern.charAt(j)) {
				j = pi[j-1];
			}
			if(searchData.charAt(i) == pattern.charAt(j)) {
				if(j == m-1) {
					result.add(i-m+1);
					j = pi[j];
				} else {
					j++;
				}
			}
		}
		return result;
		
	}
}