package com.kakao.test.api.comparator;

import java.util.Comparator;

import com.kakao.test.api.entity.County;
import com.kakao.test.api.util.SortUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CountyRateComparator implements Comparator<County>{

	@Override
	public int compare(County o1, County o2) {
		Float compareValue1 = SortUtil.stringToRateFloat(o1.getRate());
		Float compareValue2 = SortUtil.stringToRateFloat(o2.getRate());

		return compareValue1.compareTo(compareValue2);
	}
	
	
}
