package com.kakao.test.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ErrorVo {

    public final static String SUCCESS = "00";

	private String code = SUCCESS;
	private String message = "";

	public ErrorVo(String code) {
		this.code = code;
		this.message = "";
	}
	
    public ErrorVo(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

