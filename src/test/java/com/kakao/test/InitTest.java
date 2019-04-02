package com.kakao.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;

import com.kakao.test.member.entity.Member;

public class InitTest {
	
	final static String DATA_FILE_NAME = "dataset.csv";
	Member member = new Member();

	String userId = "leel2415";
	String userPass = "12341234";
	
	CSVParser parser;
	BufferedReader in;
	
	@Before
	public void init() {
		member.setUserId(userId);
		member.setUserPass(userPass);
	}
	
	@Before
	public void excelInit() throws IOException {
		in = new BufferedReader(new InputStreamReader(new FileInputStream(DATA_FILE_NAME), "EUCKR"));
    	parser = CSVFormat.EXCEL.parse(in);
	}

}
