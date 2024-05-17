package com.mfc.payment.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.domain.Payment;
import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.dto.response.PaymentHistoryResponse;
import com.mfc.payment.dto.response.PaymentResponse;
import com.mfc.payment.infrastructure.PaymentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final CashService cashService;

	@Transactional
	public CashResponse chargeCash(PaymentRequest request, UUID uuid) {
		// 결제 처리 로직
		Payment payment = Payment.builder()
			.amount(request.getAmount())
			.paymentStatus(request.getPaymentStatus())
			.paymentDate(request.getPaymentDate())
			.paymentId(request.getPaymentId())
			.uuid(uuid)
			// 기타 필드 설정
			.build();
		paymentRepository.save(payment);

		// 캐시 잔액 업데이트
		return cashService.createOrUpdateCash(uuid, request.getAmount());
	}
	@Transactional(readOnly = true)
	public PaymentHistoryResponse getPaymentHistory(UUID uuid) {
		List<Payment> payments = paymentRepository.findByUuid(uuid);

		List<PaymentResponse> paymentResponses = payments.stream()
			.map(payment -> PaymentResponse.builder()
				.amount(payment.getAmount())
				.paymentStatus(payment.getPaymentStatus())
				.paymentDate(payment.getPaymentDate())
				.build())
			.toList();

		CashResponse cashResponse = cashService.getCashBalance(uuid);

		return PaymentHistoryResponse.builder()
			.paymentResponses(paymentResponses)
			.cashResponse(cashResponse)
			.build();
	}
}
