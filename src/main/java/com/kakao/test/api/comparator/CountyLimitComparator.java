package com.kakao.test.api.comparator;

import java.util.Comparator;

import com.kakao.test.api.entity.County;
import com.kakao.test.api.util.SortUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CountyLimitComparator implements Comparator<County>{
	
	@Override
	public int compare(County o1, County o2) {
		Integer compareValue1 = SortUtil.stringToValueInteger(o1.getLimit());
		Integer compareValue2 = SortUtil.stringToValueInteger(o2.getLimit());

		return compareValue1.compareTo(compareValue2);
	}
}
