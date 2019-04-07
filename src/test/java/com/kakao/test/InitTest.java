package com.kakao.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.kakao.test.member.dao.MemberDao;
import com.kakao.test.member.entity.Member;

public class InitTest {
	
	final static String DATA_FILE_NAME = "dataset.csv";
	Member member = new Member();

	String userId = "leel2415";
	String userPass = "12341234";
	
	CSVParser parser;
	BufferedReader in;
	
	@Autowired
	private MemberDao memberDao;
	
	@Before
	public void init() {
		member.setUserId(userId);
		member.setUserPass(userPass);
		
		memberDao.save(member);
	}
	
	@Before
	public void excelInit() throws IOException {
		in = new BufferedReader(new InputStreamReader(new FileInputStream(DATA_FILE_NAME), "EUCKR"));
    	parser = CSVFormat.EXCEL.parse(in);
	}

}
