package com.mfc.payment.application;

import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.response.PaymentHistoryResponse;

/**
 * 결제 관련 서비스를 정의하는 인터페이스입니다.
 * 이 인터페이스는 캐시 충전 및 결제 내역 조회 기능을 제공합니다.
 */
public interface PaymentService {

	/**
	 * 사용자의 캐시를 충전합니다.
	 *
	 * @param request 충전 요청 정보를 담고 있는 객체
	 * @param uuid 충전을 요청한 사용자의 고유 식별자
	 */
	void chargeCash(PaymentRequest request, String uuid);

	/**
	 * 사용자의 결제 내역을 조회합니다.
	 *
	 * @param uuid 결제 내역을 조회할 사용자의 고유 식별자
	 * @return PaymentHistoryResponse 사용자의 결제 내역 정보
	 */
	PaymentHistoryResponse getPaymentHistory(String uuid);
}