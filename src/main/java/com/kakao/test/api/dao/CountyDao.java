package com.kakao.test.api.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kakao.test.api.entity.County;

public interface CountyDao extends JpaRepository<County, String>{

	@Query(value = "SELECT region, target, usage, SUPPROT_LIMIT as \"limit\" , rate, institute, mgmt, reception FROM COUNTY , COUNTY_CODE WHERE COUNTY.REGION_CODE = COUNTY_CODE.REGION_CODE", nativeQuery=true)
	public List<Map<String, String>> findCountyList();
	
	public County findByRegionCode(@Param("regionCode") String regionCode);
	public County findByCountyCodeRegion(@Param("region") String region);
}
