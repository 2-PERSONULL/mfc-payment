package com.mfc.payment.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.dto.request.TransferRequest;
import com.mfc.payment.dto.response.CashResponse;

public interface CashService {

	void createOrUpdateCash(String uuid, Double amount);

	CashResponse getCashBalance(String uuid);

	void consumeUserSettlement(TransferRequest request);

	@KafkaListener(topics = "partner-completion", groupId = "completion-group")
	@Transactional
	void consumePartnerCompletion(String message);

	@Transactional
	void cancelPayment(String userUuid, String partnerUuid, Double amount);
}
