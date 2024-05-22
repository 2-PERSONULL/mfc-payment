package com.mfc.payment.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.domain.Cash;
import com.mfc.payment.domain.CashTransfer;
import com.mfc.payment.domain.event.PartnerCompletionEvent;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.infrastructure.CashRepository;
import com.mfc.payment.infrastructure.CashTransferRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CashServiceImpl implements CashService {

	private final CashRepository cashRepository;
	private final CashTransferRepository cashTransferRepository;


	@Override
	@Transactional
	public void createOrUpdateCash(String uuid, Integer amount) {
		Cash cash = cashRepository.findByUuid(uuid)
			.map(existingCash -> Cash.builder()
				.id(existingCash.getId())
				.uuid(uuid)
				.balance(existingCash.getBalance() + amount)
				.build())
			.orElseGet(() -> Cash.builder()
				.uuid(uuid)
				.balance(amount)
				.build());

		cashRepository.save(cash);

	}

	@Override
	@Transactional(readOnly = true)
	public CashResponse getCashBalance(String uuid) {
		return cashRepository.findByUuid(uuid)
			.map(cash -> CashResponse.builder()
				.balance(cash.getBalance())
				.build())
			.orElseGet(() -> CashResponse.builder()
				.balance(0)
				.build());
	}

	@Override
	@Transactional
	public void processPartnerSettlement(String userUuid, String partnerUuid, Integer amount) {
		// 유저의 캐시 잔액 차감
		Cash userCash = cashRepository.findByUuid(userUuid)
			.orElseThrow(() -> new RuntimeException("유저의 캐시 정보를 찾을 수 없습니다."));

		Cash updatedUserCash = Cash.builder()
			.id(userCash.getId())
			.uuid(userUuid)
			.balance(userCash.getBalance() - amount)
			.build();
		cashRepository.save(updatedUserCash);

		// 캐시 전송 내역 저장
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(userUuid)
			.partnerUuid(partnerUuid)
			.amount(amount)
			.cashTransferStatus(CashTransferStatus.PENDING)
			.build();
		cashTransferRepository.save(cashTransfer);
	}

	@Override
	@KafkaListener(topics = "my-topic", groupId = "cash-service")
	public void handlePartnerCompletionEvent(PartnerCompletionEvent event) {

		// String partnerUuid = event.getPartnerUuid();
		//
		// // 대기 중인 캐시 전송 내역 조회
		// CashTransfer cashTransfer = cashTransferRepository.findByPartnerUuidAndCashTransferStatus(partnerUuid, CashTransferStatus.PENDING)
		// 	.orElseThrow(() -> new RuntimeException("대기 중인 캐시 전송 내역을 찾을 수 없습니다."));
		//
		// // 파트너의 캐시 잔액 증가
		// Cash partnerCash = cashRepository.findByUuid(partnerUuid)
		// 	.map(existingCash -> Cash.builder()
		// 		.id(existingCash.getId())
		// 		.uuid(partnerUuid)
		// 		.balance(existingCash.getBalance() + cashTransfer.getAmount())
		// 		.build())
		// 	.orElseGet(() -> Cash.builder()
		// 		.uuid(partnerUuid)
		// 		.balance(cashTransfer.getAmount())
		// 		.build());
		// cashRepository.save(partnerCash);
		//
		// // 캐시 전송 내역 상태 변경
		// cashTransfer.complete();
		// cashTransferRepository.save(cashTransfer);
	}
	@KafkaListener(topics = "my-topic", groupId = "my-group-id")
	public void consume(String message) {
		log.info("Received message: {}", message);
		// 메시지 처리 로직을 작성합니다.
	}
}