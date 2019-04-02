package com.kakao.test.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@DynamicUpdate
public class CountyCode {
	
	@Id
	@Column(updatable = false, nullable = false)
	private String regionCode;
	private String region;
}
