package com.mfc.payment.application;

import java.util.UUID;

import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.dto.response.PaymentHistoryResponse;

public interface PaymentService {
	CashResponse chargeCash(PaymentRequest request, UUID uuid);

	PaymentHistoryResponse getPaymentHistory(UUID uuid);
}
