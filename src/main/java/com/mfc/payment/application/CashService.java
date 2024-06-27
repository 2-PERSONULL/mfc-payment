package com.mfc.payment.application;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.dto.request.TransferRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.dto.response.CashTransferHistoryResponse;

public interface CashService {

	/**
	 * 사용자의 캐시를 생성하거나 업데이트합니다.
	 * @param uuid 사용자 식별자
	 * @param amount 추가할 금액
	 */
	void createOrUpdateCash(String uuid, Double amount);

	/**
	 * 사용자의 캐시 잔액을 조회합니다.
	 * @param uuid 사용자 식별자
	 * @return CashResponse 캐시 잔액 정보
	 */
	CashResponse getCashBalance(String uuid);

	/**
	 * 사용자 결제를 처리합니다.
	 * @param request 결제 요청 정보
	 */
	void consumeUserSettlement(TransferRequest request);

	/**
	 * 결제를 취소하고 금액을 환불합니다.
	 * @param userUuid 사용자 식별자
	 * @param partnerUuid 파트너 식별자
	 */
	void cancelPayment(String userUuid, String partnerUuid);

	/**
	 * 캐시 전송내역을 보여줍니다.
	 * @param userUuid 사용자 식별자
	 * @return List<CashTransferHistoryResponse> 캐시 전송 내역
	 */
	Page<CashTransferHistoryResponse> getCashTransferHistory(String userUuid,
		CashTransferStatus status, LocalDate month, Pageable pageable);


}