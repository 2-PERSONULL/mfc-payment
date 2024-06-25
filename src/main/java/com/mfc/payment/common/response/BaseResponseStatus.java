package com.mfc.payment.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {

	// 200 :요청성공
	/**
	 * 200: 요청 성공
	 **/
	SUCCESS(HttpStatus.OK, true, 200, "요청에 성공했습니다."),

	NOT_ENOUGH_CASH(HttpStatus.FORBIDDEN, false, 1001, "캐시 잔액이 부족합니다"),
	NOT_ENOUGH_ADMIN_CASH(HttpStatus.FORBIDDEN, false, 1002, "어드민 캐시 잔액이 부족합니다"),
	PAYMENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 1003, "결제 처리 중 오류가 발생했습니다"),
	INVALID_PAYMENT_REQUEST(HttpStatus.BAD_REQUEST, false, 1004, "유효하지 않은 결제 요청입니다"),

	NO_EXIST_MEMBERS(HttpStatus.NOT_FOUND, false, 2106, "존재하지 않는 멤버 정보입니다"),
	ADMIN_CASH_NOT_FOUND(HttpStatus.NOT_FOUND, false, 2107, "어드민 캐시 정보를 찾을 수 없습니다"),
	USER_CASH_NOT_FOUND(HttpStatus.NOT_FOUND, false, 2108, "사용자의 캐시 정보를 찾을 수 없습니다");

	private final HttpStatusCode httpStatusCode;
	private final boolean isSuccess;
	private final int code;
	private final String message;
}