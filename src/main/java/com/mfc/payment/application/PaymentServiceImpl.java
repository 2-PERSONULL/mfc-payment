package com.mfc.payment.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.exception.BaseException;
import com.mfc.payment.common.response.BaseResponseStatus;
import com.mfc.payment.domain.Payment;
import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.response.PaymentHistoryResponse;
import com.mfc.payment.dto.response.PaymentResponse;
import com.mfc.payment.infrastructure.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final CashService cashService;

	@Override
	@Transactional
	public void chargeCash(PaymentRequest request, String uuid) {
		validatePaymentRequest(request);
		try {
			Payment payment = createPayment(request, uuid);
			paymentRepository.save(payment);
			cashService.createOrUpdateCash(uuid, request.getAmount());
			log.info("Successfully charged cash for user: {}, amount: {}", uuid, request.getAmount());
		} catch (Exception e) {
			log.error("Failed to charge cash for user: {}", uuid, e);
			throw new BaseException(BaseResponseStatus.PAYMENT_PROCESSING_FAILED);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentHistoryResponse getPaymentHistory(String uuid) {
		List<Payment> payments = paymentRepository.findByUuid(uuid);
		if (payments.isEmpty()) {
			log.info("No payment history found for user: {}", uuid);
		} else {
			log.info("Retrieved {} payment records for user: {}", payments.size(), uuid);
		}
		List<PaymentResponse> paymentResponses = convertToPaymentResponses(payments);
		return PaymentHistoryResponse.builder()
			.paymentResponses(paymentResponses)
			.build();
	}

	private void validatePaymentRequest(PaymentRequest request) {
		if (request == null || request.getAmount() == null || request.getAmount() <= 0) {
			throw new BaseException(BaseResponseStatus.INVALID_PAYMENT_REQUEST);
		}
	}

	private Payment createPayment(PaymentRequest request, String uuid) {
		return Payment.builder()
			.amount(request.getAmount())
			.paymentStatus(request.getPaymentStatus())
			.paymentDate(request.getPaymentDate())
			.paymentId(request.getPaymentId())
			.uuid(uuid)
			.build();
	}

	private List<PaymentResponse> convertToPaymentResponses(List<Payment> payments) {
		return payments.stream()
			.map(payment -> PaymentResponse.builder()
				.amount(payment.getAmount())
				.paymentStatus(payment.getPaymentStatus())
				.paymentDate(payment.getPaymentDate())
				.build())
			.toList();
	}
}