package com.kakao.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import com.kakao.test.api.dao.CountyDao;
import com.kakao.test.api.entity.County;
import com.kakao.test.api.entity.CountyCode;
import com.kakao.test.common.util.ObjectConvertUtil;
import com.kakao.test.common.util.TestUtils;

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
}
