package com.kakao.test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakao.test.api.dao.CountyDao;
import com.kakao.test.api.entity.County;
import com.kakao.test.common.util.CsvUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * CsvTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CsvTest extends InitTest{

	@Autowired
	private CountyDao countyDao;
	
    @Test
    public void excelFileRead() throws IOException{
    	assertThat(parser.getRecords().size(), not(0));
    }
    
    @Test
    public void dataMapping() throws IOException {
    	List<County> countyList = new ArrayList<County>();
    	
    	for(CSVRecord record : parser) {
    		if(record.getRecordNumber() != 1) {
    			log.debug(record.toString());
    			countyList.add(convertMappingVo(record));
    		}
    	}
    	assertThat(countyList.size(), not(0));
    }
    
    @Test
    @Transactional
    public void dataSave() throws IOException {
    	County county = convertMappingVo(parser.getRecords().get(1));
    	countyDao.save(county);
    	
    	assert countyDao.getOne("1") != null;
    }
    
    @Test
    @Transactional
    public void dataAllSave() throws IOException {
    	List<CSVRecord> list = CsvUtil.readCsv();
    	List<County> countyList = new ArrayList<>();
    	
    	for(CSVRecord record : list) {
    		countyList.add(convertMappingVo(record));
    	}
    	
    	assertThat(countyDao.saveAll(countyList).size(), is(98));
    }
    
    private County convertMappingVo(CSVRecord record) {
    	County county = new County();
    	
    	county.setId(record.get(0));
    	county.setRegionCode("reg" + String.format("%4s", record.get(0)).replace(' ', '0'));
    	county.setTarget(record.get(2));
    	county.setUsage(record.get(3));
    	county.setLimit(record.get(4));
    	county.setRate(record.get(5));
    	county.setInstitute(record.get(6));
    	county.setMgmt(record.get(7));
    	county.setReception(record.get(8));
    	return county;
    }
}