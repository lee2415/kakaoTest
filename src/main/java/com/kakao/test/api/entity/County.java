package com.kakao.test.api.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@DynamicUpdate
public class County {
	
	public County() {
		countyCode = new CountyCode();
	}
	
	@JsonIgnore
	private String id;
	
	@Id
	@Column(updatable = false, nullable = false)
	private String regionCode;
	// 지원대상 
	private String target;
	// 용도
	private String usage;
	// 지원한도 
	@Column(name = "supprotLimit")
	private String limit;
	// 이차보전 
	private String rate;
	// 추천기관 
	private String institute;
	// 관리점 
	private String mgmt;
	// 취급점 
	private String reception;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "regionCode")
	private CountyCode countyCode;
	
	public void setRegion(String region) {
		countyCode.setRegion(region);
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
		countyCode.setRegionCode(regionCode);
	}
	public void setCountyCode(CountyCode countyCode) {
		if(!StringUtils.isEmpty(countyCode.getRegion())){
			this.countyCode.setRegion(countyCode.getRegion());
		}
		if(!StringUtils.isEmpty(countyCode.getRegionCode())){
			this.countyCode.setRegionCode(countyCode.getRegionCode());
		}
		if(countyCode.getLocation() != null) {
			this.countyCode.setLocation(countyCode.getLocation());
		}
	}
	
}
