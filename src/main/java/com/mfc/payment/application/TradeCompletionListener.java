package com.mfc.payment.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.domain.AdminCash;
import com.mfc.payment.domain.Cash;
import com.mfc.payment.domain.CashTransfer;
import com.mfc.payment.dto.kafka.TradeSettledEventDto;
import com.mfc.payment.infrastructure.AdminCashRepository;
import com.mfc.payment.infrastructure.CashRepository;
import com.mfc.payment.infrastructure.CashTransferRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeCompletionListener {

	private final AdminCashRepository adminCashRepository;
	private final CashRepository cashRepository;
	private final CashTransferRepository cashTransferRepository;

	@KafkaListener(topics = "partner-completion", containerFactory = "kafkaListenerContainerFactory")
	@Transactional
	public void consumePartnerCompletion(TradeSettledEventDto dto) {
		log.info("Received partner completion message: {}", dto);

		try {
			String userUuid = dto.getUserUuid();
			String partnerUuid = dto.getPartnerUuid();
			Double amount = dto.getAmount();

			subtractFromAdminCash(amount);
			depositToPartnerCash(partnerUuid, amount);
			createCashTransfer(userUuid, partnerUuid, amount);
		} catch (Exception e) {
			log.error("Failed to consume partner completion message", e);
			throw e;
		}
	}

	private void subtractFromAdminCash(Double amount) {
		AdminCash adminCash = adminCashRepository.findById(1L)
			.orElse(AdminCash.builder()
				.balance(0.0)
				.build());

		adminCash.subtractBalance(amount);
		adminCashRepository.save(adminCash);
	}

	private void depositToPartnerCash(String partnerUuid, Double amount) {
		Cash partnerCash = cashRepository.findByUuid(partnerUuid)
			.map(existingCash -> {
				existingCash.addBalance(amount);
				return existingCash;
			})
			.orElse(Cash.builder()
				.uuid(partnerUuid)
				.balance(amount)
				.build());

		cashRepository.save(partnerCash);
	}

	private void createCashTransfer(String userUuid, String partnerUuid, Double amount) {
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(userUuid)
			.partnerUuid(partnerUuid)
			.amount(amount)
			.status(CashTransferStatus.COMPLETED)
			.build();

		cashTransferRepository.save(cashTransfer);
	}
}