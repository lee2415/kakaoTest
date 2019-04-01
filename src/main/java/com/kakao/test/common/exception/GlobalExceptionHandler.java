package com.kakao.test.common.exception;

import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakao.test.common.ResponseBase;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice(value="${spring.application.base-package}", annotations=Controller.class)
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * BizException
	 *
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = BizException.class)
	public @ResponseBody ResponseEntity<?> bizExceptionErrorHandler(BizException e) throws Exception {
		log.error(e.getMessage(), e);

		try {
			String code = e.getExceptionCode();
			String message = e.getExceptionMessage();
			ErrorVo errorVo = new ErrorVo(code, message);
			
			log.error(errorVo.toString());
			
			return ResponseBase.error(errorVo);
		} catch (NoSuchMessageException ex) {
			return ResponseBase.error(getDefaultErrorVo(e));
		}
	}

	@ExceptionHandler(value = Exception.class)
	public @ResponseBody ResponseEntity<?> defaultErrorHandler(Exception e) throws Exception {

		log.error(e.getMessage(), e);

		return ResponseBase.error(getDefaultErrorVo(e));
	}
	
	private ErrorVo getDefaultErrorVo(Exception e) {
		String code     = "ERROR.999";
		String message  = e.getMessage();
		ErrorVo errorVo = new ErrorVo(code, message);

		log.error(e.getMessage(), e);

		return errorVo;
	}
}

