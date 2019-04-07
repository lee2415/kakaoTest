package com.kakao.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakao.test.api.dao.CountyDao;
import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyCode;
import com.kakao.test.api.util.KmpUtil;
import com.kakao.test.api.util.ObjectConvertUtil;
import com.kakao.test.api.util.SearchWordUtil;
import com.kakao.test.api.util.SortUtil;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApiTest {
	
	@Autowired
	private CountyDao countyDao;
	
	@Test
	@Transactional
	public void findRegion() {
		log.debug(countyDao.findByCountyCodeRegion("강릉시").toString());
		assertThat(countyDao.findByCountyCodeRegion("강릉시").getRegionCode(), is("reg0001"));
	}
	
	@Test
	@Transactional
	public void update() {
		County county = new County();
		CountyCode countyCode = new CountyCode();
		county.setCountyCode(countyCode);
		county.setRate("5%");
		
		County selectCounty = countyDao.findByRegionCode("reg0001");
		
		log.debug(selectCounty.toString());
		
//		TestUtils.copyProperties(county, selectCounty);
		ObjectConvertUtil.copyToConutyNotNullData(selectCounty, county);
		
		log.debug(selectCounty.toString());
		
		log.debug("update : " + countyDao.save(selectCounty).toString());
		log.debug("result : " + countyDao.findByRegionCode("reg0001").toString());
		
		assertThat(countyDao.findByRegionCode("reg0001").getRate(), is("5%"));
		assertThat(countyDao.findByRegionCode("reg0001").getTarget(), is(notNullValue()));
	}
	
	@Test
	@Transactional
	public void sortAndLimit() {
		List<County> list = SortUtil.sortAndLimit(countyDao.findAll(), 10);	
		for(County county : list) {
			log.debug("region : {}, limit : {}, rate : {}", county.getCountyCode().getRegion(), county.getLimit(), county.getRate());
		}
		assertThat(list.size(), is(10));
	}	
	
	@Test
	@Transactional
	public void sortAndRate() {
		List<County> list = SortUtil.sortAndRate(countyDao.findAll());
		for(County county : list) {
			log.debug("limit : {}, rate : {}", county.getLimit(), county.getRate());
		}
	}
	
	@Test
	public void useLikeSearch() throws IOException {
		String input = "철수는 충남 대천에 살고 있는데, 은퇴하고 시설과 관리 비즈니스를 하기를 원한다. 시설 관리 관련 사업자들을 만나보니 50백만 관련 사업을 하려면 대체로 5 억은 필요하고, 이차보전 비 율은 2.5% 이내가 좋다는 의견을 듣고 정부에서 운영하는 지자체 협약 지원정보를 검색한다.";
		log.debug(input.replaceAll(" ", ""));
		
		input = input.replaceAll(" ", "");
		
		Map<String, Object> resultMap = SearchWordUtil.searchWord(input);
		List<String> findUsage = (List<String>) resultMap.get("usage");
		
		List<County> countyList = null;
		if(findUsage.size() != 2) {
			countyList = countyDao.findByUsageContaining(findUsage.get(0));
		} else {
			countyList = countyDao.findAll();
		}
		assertThat(countyList.size(), is(34));
		
		// 조회한 데이터 기준으로 정렬 
		SortUtil.sortAndLimit(countyList);
		
		// 지원금액을 검색하여 그 이상 데이터 삭제 
		Integer limit = (Integer) resultMap.get("limit");
		// 검색한 금액 보다 큰 데이터는 삭제 
		int checkLimitIndex = SortUtil.checkLimitIndex(countyList, limit);
		// 검색한 금액 보다 큰 데이터가 없을 경우 list size를 return 
		if(checkLimitIndex != countyList.size()) {
			countyList = countyList.subList(0, checkLimitIndex);
		}
		assertThat(countyList.size(), is(25));
		SortUtil.sortAndRate(countyList);
		
		Float rate = (Float) resultMap.get("rate");
		int checkRateIndex = SortUtil.checkRateIndex(countyList, rate);
		if(checkRateIndex != countyList.size()) {
			countyList = countyList.subList(0, checkRateIndex);
		}
		
		assertThat(countyList.size(), is(11));
		
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
	
	@Test
	public void searchWord() {
		String input = "철수는 충남 대천에 살고 있는데, 은퇴하고 시설 관리 비즈니스를 하기를 원한다. 시설 관리 관련 사업자들을 만나보니 관련 사업을 하려면 대체로 5 억은 필요하고, 이차보전 비 율은 2.5% 이내가 좋다는 의견을 듣고 정부에서 운영하는 지자체 협약 지원정보를 검색한다.";
		String pattern = "시설";
		
		List<Integer> list = KmpUtil.search(input, pattern);
		log.debug(list.size() + " : " + list.toString());
		
		assertThat(input.substring(list.get(0), list.get(0) + pattern.length()), is("시설"));
	}
	
	@Test
	public void randomGenertatorGeolocation() {
		
		// 34 ~ 38
		// 124 ~ 128
		// 소수점은 5자리수까지
		
		double x1 = 34.1234;
		double y1 = 124.1234;
		
		double x2 = 36.1234;
		double y2 = 124.1234;
		
		double x3 = 36.1234;
		double y3 = 125.1234;
		
		System.out.println(SortUtil.distance(x1, y1, x2, y2));
		System.out.println(SortUtil.distance(x1, y1, x3, y3));
		
	}
	
}
