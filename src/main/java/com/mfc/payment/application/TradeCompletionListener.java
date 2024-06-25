package com.mfc.payment.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.common.exception.BaseException;
import com.mfc.payment.common.response.BaseResponseStatus;
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
			processTradeSettlement(dto);
			log.info("Successfully processed trade settlement for user: {}, partner: {}", dto.getUserUuid(), dto.getPartnerUuid());
		} catch (BaseException e) {
			log.error("Failed to process trade settlement: {}", e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error occurred while processing trade settlement", e);
			throw e;
		}
	}

	private void processTradeSettlement(TradeSettledEventDto dto) {
		String userUuid = dto.getUserUuid();
		String partnerUuid = dto.getPartnerUuid();
		Double amount = dto.getAmount();

		subtractFromAdminCash(amount);
		depositToPartnerCash(partnerUuid, amount);
		createCashTransfer(userUuid, partnerUuid, amount);
	}

	private void subtractFromAdminCash(Double amount) {
		AdminCash adminCash = getAdminCash();
		if (adminCash.getBalance() < amount) {
			throw new BaseException(BaseResponseStatus.NOT_ENOUGH_ADMIN_CASH);
		}
		adminCash.subtractBalance(amount);
		adminCashRepository.save(adminCash);
	}

	private AdminCash getAdminCash() {
		return adminCashRepository.findById(1L)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.ADMIN_CASH_NOT_FOUND));
	}

	private void depositToPartnerCash(String partnerUuid, Double amount) {
		Cash partnerCash = getCashByUuid(partnerUuid);
		partnerCash.addBalance(amount);
		cashRepository.save(partnerCash);
	}

	private Cash getCashByUuid(String uuid) {
		return cashRepository.findByUuid(uuid)
			.orElseGet(() -> Cash.builder()
				.uuid(uuid)
				.balance(0.0)
				.build());
	}

	private void createCashTransfer(String userUuid, String partnerUuid, Double amount) {
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(userUuid)
			.partnerUuid(partnerUuid)
			.amount(amount)
			.status(CashTransferStatus.SETTLEMENT_COMPLETED)
			.build();

		cashTransferRepository.save(cashTransfer);
	}
}