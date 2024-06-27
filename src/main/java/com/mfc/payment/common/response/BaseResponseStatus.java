package com.mfc.payment.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {

	// 2xx: 성공
	SUCCESS(HttpStatus.OK, true, 200, "요청에 성공했습니다."),

	// 4xx: 클라이언트 오류
	// 400: 잘못된 요청
	INVALID_PAYMENT_REQUEST(HttpStatus.BAD_REQUEST, false, 400, "유효하지 않은 결제 요청입니다"),
	INVALID_TRANSFER_REQUEST(HttpStatus.BAD_REQUEST, false, 400, "유효하지 않은 이체 요청입니다"),

	// 403: 권한 없음
	NOT_ENOUGH_CASH(HttpStatus.FORBIDDEN, false, 403, "캐시 잔액이 부족합니다"),
	NOT_ENOUGH_ADMIN_CASH(HttpStatus.FORBIDDEN, false, 403, "어드민 캐시 잔액이 부족합니다"),

	// 404: 리소스를 찾을 수 없음
	NO_EXIST_MEMBERS(HttpStatus.NOT_FOUND, false, 404, "존재하지 않는 멤버 정보입니다"),
	ADMIN_CASH_NOT_FOUND(HttpStatus.NOT_FOUND, false, 404, "어드민 캐시 정보를 찾을 수 없습니다"),
	USER_CASH_NOT_FOUND(HttpStatus.NOT_FOUND, false, 404, "사용자의 캐시 정보를 찾을 수 없습니다"),
	CASH_TRANSFER_NOT_FOUND(HttpStatus.NOT_FOUND, false, 404, "해당하는 캐시 이체 내역을 찾을 수 없습니다"),

	// 409: 충돌
	ALREADY_CANCELLED(HttpStatus.CONFLICT, false, 409, "이미 취소된 거래입니다"),

	// 5xx: 서버 오류
	// 500: 내부 서버 오류
	PAYMENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 500, "결제 처리 중 오류가 발생했습니다");

	private final HttpStatusCode httpStatusCode;
	private final boolean isSuccess;
	private final int code;
	private final String message;
}