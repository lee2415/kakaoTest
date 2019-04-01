package com.kakao.test.common.exception;

public class BizException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String exceptionCode;
	private String exceptionMessage;
	
	public BizException(String exceptionCode) {
		this(exceptionCode, null, null);
	}
	
	public BizException(String exceptionCode, Throwable cause) {
		this(exceptionCode, cause, null);
	}
	
	public BizException(String exceptionCode, String message) {
		this(exceptionCode, null, message);
	}
	
	public BizException(String exceptionCode, Throwable cause, String message) {
		super(cause!=null?cause.getMessage():exceptionCode, cause);
		this.exceptionCode = exceptionCode;
		this.exceptionMessage = message;
	}
	
	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
	
	public String getExceptionCode() {
		return exceptionCode;
	}
	
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	
}
