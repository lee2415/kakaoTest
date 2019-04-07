package com.kakao.test.api.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

import com.kakao.test.api.vo.RegionVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchWordUtil {
	
	private static List<RegionVo> regionList;
	
	/**
	 * 신문기사 정보를 입력받아 필요한 내용을 추출하는 Method
	 * 결과로 용도, 지원금액, 이차보전비율, 위치에 대한 정보를 return한다. 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> searchWord(String input) throws IOException{
		// 지역 검색을 위해 지역정보를 저장한 csv를 load한다. 
		if(regionList == null) {
			loadData();
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
	
		// 문자열 검색을 위해 KMP 알고리즘을 활용하여 문자열 검색 진행.
		// 용도 문자열 검색
		List<String> findUsage = new ArrayList<>();
		// 용도에 대한 정보 전체 조회 
		for(int i=0;i<Usage.values().length;i++) {
			List<Integer> usageResultIndex = KmpUtil.search(input, Usage.values()[i].getUsage());
			if(usageResultIndex.size() > 0) {
				findUsage.add(Usage.values()[i].getUsage());
			}
		}
		log.debug(findUsage.toString());
		resultMap.put("usage", findUsage);

		// 지원금액 문자열 검색
		Integer limit = -1;
		for(int i=0;i<Limit.values().length;i++) {
			List<Integer> limitResultIndex = KmpUtil.search(input, Limit.values()[i].getLimit());
			Integer result = checkWordLimit(limitResultIndex, input, Limit.values()[i].getLimit());
			// 가장 큰 지원금액에 대한 문자열을 검색하여 저장 
			if(limit.compareTo(result) == -1) {
				limit = result;
			}
		}
		log.debug("limit : {}", limit);
		resultMap.put("limit", limit);
		
		// 이차보전 비율 검색
		List<Integer> rateResultIndex = KmpUtil.search(input, "%");
		Float rate = checkWordRate(rateResultIndex, input);
		log.debug("rate : {}", rate);
		resultMap.put("rate", rate);
		
		
		// 위치 검색
		int regionIndex = -1;
		for(int i=0;i<regionList.size();i++) {
			// 지역 List로 해당 지역이 있는지 조회하여, 있을 경우 해당 list의 index값을 저장
			// 도 --> 시 순으로 검색되기 때문에 나중에 검색된 데이터가 더 상세한 데이터로 덮어 씌움 
			List<Integer> regionResultIndex = KmpUtil.search(input, regionList.get(i).getRegionName());
			if(regionResultIndex.size() > 0) {
				regionIndex = i;
			}
		}
		// 지역 검색이 되었을 경우 해당 값을 return 
		if(regionIndex != -1) {
			resultMap.put("x", regionList.get(regionIndex).getX());
			resultMap.put("y", regionList.get(regionIndex).getY());
		}
		return resultMap;
	}
	
	/**
	 * 지원금액에 대한 문자열 검색을 하여, 가장 큰 값을 return하는 Method
	 * return하는 데이터는 숫자화하여 비교할 수 있는 데이터로 return
	 * @param list
	 * @param input
	 * @param pattern
	 * @return
	 */
	private static Integer checkWordLimit(List<Integer> list, String input, String pattern) {
		Integer resultValue = 0;
		
		for(Integer index : list) {
			
			int j = 1;
			while(input.charAt(index-j) > 47 && input.charAt(index-j) < 58) {
				j++;
			}
			Integer compareValue = SortUtil.stringToValueInteger(input.substring(index-j,index + pattern.length()));
			if(resultValue.compareTo(compareValue) == -1) {
				resultValue = compareValue;
			}
		}
		return resultValue;
	}
	
	/**
	 * 이차보전 비율에 대한 문자열 검색을 하여, 가장 큰 값을 return하는 Method
	 * return하는 데이터는 숫자화하여 비교할 수 있는 데이터로 return 
	 * @param list
	 * @param input
	 * @return
	 */
	private static Float checkWordRate(List<Integer> list, String input) {
		Float resultValue = (float) -1;

		for(Integer index : list) {

			int j = 1;
			while((input.charAt(index-j) > 47 && input.charAt(index-j) < 58) || input.charAt(index-j) == '.'){
				j++;
			}
			Float compareValue = SortUtil.stringToRateFloat(input.substring(index-j,index));
			if(resultValue.compareTo(compareValue) == -1) {
				resultValue = compareValue;
			}
		}
		
		return resultValue;
	}
	
	/**
	 * 지역에 대한 정보를 검색할때 사용하기 위해 csv에 저장된 지역정보를 읽어와 저장 
	 * 도/시 정보만 입력되어 있음. 
	 * @throws IOException
	 */
	private static void loadData() throws IOException {
		regionList = new ArrayList<>();
		
		List<CSVRecord> csvList = CsvUtil.readRegionCsv();
		// 좌표 계산을 위해 임의의 랜덤값으로 해당 지역에 대한 위치값 부여, 추후 가능하다면 실제 위치값을 부여 하여 실제 거리 측정 가능
		for(CSVRecord record : csvList) {
			RegionVo regionVo = new RegionVo();
			regionVo.setRegionName(record.get(0));
			regionVo.setX((double)Math.random() * 5 + 34);
			regionVo.setY((double)Math.random() * 5 + 124);
			
			regionList.add(regionVo);
		}
	}

	static enum Usage {
		driving("운전"),
		installation("시설");
		
		private String usage;
		
		Usage (String usage){
			this.usage = usage;
		}
		
		public String getUsage() {
			return usage;
		}
	}
	
	static enum Limit {
		recommand("추천금액"),
		hundred("백만"),
		thousand("천만"),
		hundredmillion("억");
		
		private String limit;
		
		Limit(String limit){
			this.limit = limit;
		}
		
		public String getLimit() {
			return limit;
		}
	}
	
	
	
}
