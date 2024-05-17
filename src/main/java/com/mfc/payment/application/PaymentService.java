package com.mfc.payment.application;

import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.response.PaymentHistoryResponse;

public interface PaymentService {
	void chargeCash(PaymentRequest request, String uuid);

	PaymentHistoryResponse getPaymentHistory(String uuid);
}
