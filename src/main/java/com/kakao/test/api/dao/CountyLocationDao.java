package com.kakao.test.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakao.test.api.entity.County;

public interface CountyLocationDao extends JpaRepository<County, String>{
}
