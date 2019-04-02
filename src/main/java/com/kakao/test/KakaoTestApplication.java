package com.kakao.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kakao.test.api.service.CountyService;

@SpringBootApplication
public class KakaoTestApplication implements CommandLineRunner{

	@Autowired
	private CountyService countyService;
	
	public static void main(String[] args) {
		SpringApplication.run(KakaoTestApplication.class, args);
	}
	
	@Override
    public void run(String... arg0) throws Exception {
		countyService.save();
    }

}
