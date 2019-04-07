package com.kakao.test.api.util;

public class NumberUtil {
	
	/**
	 * String 문자열이 숫자인지 체크
	 * @param s
	 * @return
	 */
	public static boolean isNumeric(String s) {  
        return s.matches("[-+]?\\d*\\.?\\d+");  
    }  
}
