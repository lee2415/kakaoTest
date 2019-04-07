package com.kakao.test.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@DynamicUpdate
public class CountyLocation {
	
	@Id
	@Column(updatable = false, nullable = false)
	private String regionCode;
	private float x;
	private float y;
}
