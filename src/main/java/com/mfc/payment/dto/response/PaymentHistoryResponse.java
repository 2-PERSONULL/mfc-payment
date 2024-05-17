package com.mfc.payment.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentHistoryResponse {
	private List<PaymentResponse> paymentResponses;
	private CashResponse cashResponse;
}