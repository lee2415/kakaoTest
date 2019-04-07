package com.kakao.test.api.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lombok.extern.slf4j.Slf4j;

/**
 * CsvUtil
 */
@Slf4j
public class CsvUtil {
	
	private final static String DATA_FILE_NAME = "dataset.csv";
	private final static String REGION_FILE_NAME = "region.csv";
	
	/**
	 * 지자체 정보를 읽어오기 위한 Method 
	 * @return
	 * @throws IOException
	 */
    public static List<CSVRecord> readCsv() throws IOException {
    	
    	// 파일을 읽어와 저장, 한글깨짐으로 인해 인코딩설정하여 저장 
    	BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(DATA_FILE_NAME), "EUCKR"));
    	CSVParser parser = CSVFormat.EXCEL.parse(in);
    	
    	List<CSVRecord> resultList = parser.getRecords();
    	
    	// 헤더 영역을 삭제 한다. 
    	resultList.remove(0);
    	
    	return resultList;
    }

    /**
     * 우리나라 지명정보를 읽어오기 위한 Method
     * @return
     * @throws IOException
     */
    public static List<CSVRecord> readRegionCsv() throws IOException {
    	BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(REGION_FILE_NAME), "EUCKR"));
    	CSVParser parser = CSVFormat.EXCEL.parse(in);
    	
    	List<CSVRecord> resultList = parser.getRecords();
    	
    	return resultList;
    }
}