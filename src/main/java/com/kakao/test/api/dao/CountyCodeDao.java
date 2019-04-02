package com.kakao.test.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakao.test.api.entity.CountyCode;

public interface CountyCodeDao extends JpaRepository<CountyCode, String>{
	
}
