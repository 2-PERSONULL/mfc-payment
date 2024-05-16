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

	/**
	 * 400 : security 에러
	 */
	WRONG_JWT_TOKEN(HttpStatus.UNAUTHORIZED, false, 401, "다시 로그인 해주세요"),

	/**
	 * 900: 기타 에러
	 */
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 900, "Internal server error"),

	/**
	 * 1000 : Order Service Error
	 */
	ALREADY_PAID_ORDER_ID(HttpStatus.BAD_REQUEST, false, 1000, "이미 결제된 주문번호입니다"),
	DOSE_NOT_EXIST_PAYMENT(HttpStatus.BAD_REQUEST, false, 1001, "결제내역이 존재하지 않습니다"),
	CANCELED_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, false, 1002, "취소 금액 한도를 초과하였습니다"),
	PAYMENT_DATA_TRANSFER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 1003, "결제 정산 정보 전송에 실패하였습니다"),

	/**
	 * 2000 : members Service Error
	 */
	// Token, Code
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, false, 2001, "토큰이 만료되었습니다."),
	TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, false, 2002, "토큰이 유효하지 않습니다."),
	TOKEN_NULL(HttpStatus.UNAUTHORIZED, false, 2003, "토큰이 존재하지 않습니다."),
	JWT_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 2004, "토큰 생성에 실패했습니다."),
	JWT_VALID_FAILED(HttpStatus.UNAUTHORIZED, false, 2005, "토큰 검증에 실패했습니다."),
	EXPIRED_AUTH_CODE(HttpStatus.UNAUTHORIZED, false, 2006, "인증번호가 만료되었거나 존재하지 않는 멤버입니다."),
	WRONG_AUTH_CODE(HttpStatus.UNAUTHORIZED, false, 2007, "인증번호가 일치하지 않습니다."),

	// Members
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, false, 2100, "사용중인 이메일입니다."),
	DUPLICATED_MEMBERS(HttpStatus.CONFLICT, false, 2101, "이미 가입된 멤버입니다."),
	MASSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 2102, "인증번호 전송에 실패했습니다."),
	MASSAGE_VALID_FAILED(HttpStatus.UNAUTHORIZED, false, 2103, "인증번호가 일치하지 않습니다."),
	FAILED_TO_LOGIN(HttpStatus.UNAUTHORIZED, false, 2104, "아이디 또는 패스워드를 다시 확인하세요."),
	WITHDRAWAL_MEMBERS(HttpStatus.FORBIDDEN, false, 2105, "탈퇴한 회원입니다."),
	NO_EXIST_MEMBERS(HttpStatus.NOT_FOUND, false, 2106, "존재하지 않는 멤버 정보입니다."),
	MEMBERS_STATUS_IS_NOT_FOUND(HttpStatus.NOT_FOUND, false, 2107, "존재하지 않는 멤버 상태입니다."),
	PASSWORD_SAME_FAILED(HttpStatus.BAD_REQUEST, false, 2108, "현재 사용중인 비밀번호 입니다."),
	PASSWORD_CONTAIN_NUM_FAILED(HttpStatus.BAD_REQUEST, false, 2109, "휴대폰 번호를 포함한 비밀번호 입니다."),
	PASSWORD_CONTAIN_EMAIL_FAILED(HttpStatus.BAD_REQUEST, false, 2110, "이메일이 포함된 비밀번호 입니다."),
	DORMANCY_MEMBERS(HttpStatus.FORBIDDEN, false, 2111, "휴면 회원입니다."),

	// Address
	NO_EXIST_ADDRESS(HttpStatus.NOT_FOUND, false, 2300, "존재하지 않는 주소입니다."),

	/**
	 * 5000 : Cart & WishProductList Service Error
	 */
	NO_DATA(HttpStatus.BAD_REQUEST, false, 6001, "존재하지 않는 정보입니다"),
	ALREADY_ADDED_PRODUCT(HttpStatus.CONFLICT, false, 6002, "이미 장바구니에 존재하는 상품입니다"),
	ALREADY_ADDED_WISH_PRODUCT(HttpStatus.CONFLICT, false, 6003, "이미 찜한 상품입니다"),

	/**
	 * 7000 : item (image & brand & option & category)
	 */
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, false, 7001, "존재하지 않는 상품입니다."),
	NO_PRODUCT_WITH_BRAND(HttpStatus.BAD_REQUEST, false, 7002, "브랜드 정보가 없는 상품입니다."),
	BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, false, 7003, "존재하지 않는 브랜드입니다."),
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, false, 7004, "존재하지 않는 리뷰입니다."),
	ITEM_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, false, 7005, "존재하지 않는 상품 옵션입니다."),
	ITEM_STOCK_NOT_ENOUGH(HttpStatus.BAD_REQUEST, false, 7006, "상품 재고가 부족합니다."),
	ITEM_STOCK_RESTORE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, false, 7007, "상품 재고 복구에 실패했습니다."),
	BUNDLE_NOT_FOUND(HttpStatus.NOT_FOUND, false, 7008, "존재하지 않는 묶음입니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, false, 7009, "존재하지 않는 카테고리입니다."),

	/**
	 * 8000 : purchase Service Error
	 */
	PURCHASE_NOT_FOUND(HttpStatus.NOT_FOUND, false, 8001, "존재하지 않는 주문입니다."),
	PURCHASE_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, false, 8002, "존재하지 않는 주문 상품입니다."),
	PURCHASE_LIST_DUPLICATE(HttpStatus.CONFLICT, false, 8002, "이미 주문한 상품입니다. 주문 상품을 확인해주세요."),

	/**
	 * 9000 : review Service Error
	 */
	REVIEW_ALREADY_WRITTEN(HttpStatus.BAD_REQUEST, false, 9001, "이미 리뷰를 작성한 구매목록입니다."),
	REVIEW_IMAGE_LIMIT(HttpStatus.BAD_REQUEST, false, 9002, "리뷰 이미지는 최대 3개까지 등록 가능합니다."),
	REVIEW_IMAGE_AUTHORITY(HttpStatus.BAD_REQUEST, false, 9003, "리뷰 이미지를 등록할 권한이 없습니다."),
	REVIEW_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, false, 9004, "존재하지 않는 리뷰 이미지입니다."),

	/**
	 * 10000 : clip Service Error
	 */

	CLIP_ITEM_ALREADY_ADDED(HttpStatus.BAD_REQUEST, false, 10001, "이미 좋아요한 상품입니다."),
	CLIP_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, false, 10002, "좋아요한 상품이 아닙니다."),
	CLIP_CATEGORY_ALREADY_ADDED(HttpStatus.BAD_REQUEST, false, 10003, "이미 좋아요한 카테고리입니다."),
	CLIP_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, false, 10004, "좋아요한 카테고리가 아닙니다."),
	CLIP_CATEGORY_NOT_DELETED(HttpStatus.BAD_REQUEST, false, 10005, "카테고리 좋아요 삭제에 실패했습니다.");

	private final HttpStatusCode httpStatusCode;
	private final boolean isSuccess;
	private final int code;
	private final String message;

}
