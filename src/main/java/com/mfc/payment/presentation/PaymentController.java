package com.mfc.payment.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mfc.payment.application.PaymentService;
import com.mfc.payment.common.response.BaseResponse;
import com.mfc.payment.dto.response.PaymentHistoryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
	private final PaymentService paymentService;
	@GetMapping("/history")
	public BaseResponse<PaymentHistoryResponse> paymentHistory(@RequestHeader String uuid) {
		// 결제 내역 조회 서비스 호출
		PaymentHistoryResponse response = paymentService.getPaymentHistory(uuid);

		return new BaseResponse<>(response);
	}
}