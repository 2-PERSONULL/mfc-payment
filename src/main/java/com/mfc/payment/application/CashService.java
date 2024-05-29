package com.mfc.payment.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.dto.response.CashResponse;

public interface CashService {

	void createOrUpdateCash(String uuid, Double amount);

	CashResponse getCashBalance(String uuid);

	@KafkaListener(topics = "user-settlement", groupId = "settlement-group")
	@Transactional
	void consumeUserSettlement(String message);

	@KafkaListener(topics = "partner-completion", groupId = "completion-group")
	@Transactional
	void consumePartnerCompletion(String message);
}
