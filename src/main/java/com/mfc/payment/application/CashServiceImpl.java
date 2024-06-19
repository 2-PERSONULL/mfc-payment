package com.mfc.payment.application;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.domain.AdminCash;
import com.mfc.payment.domain.Cash;
import com.mfc.payment.domain.CashTransfer;
import com.mfc.payment.dto.kafka.PaymentCompletedEvent;
import com.mfc.payment.dto.request.TransferRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.infrastructure.AdminCashRepository;
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
	private final AdminCashRepository adminCashRepository;
	private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

	@Override
	@Transactional
	public void createOrUpdateCash(String uuid, Double amount) {
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
				.balance(0.0)
				.build());
	}

	@Override
	@Transactional
	public void consumeUserSettlement(TransferRequest request) {

		// 유저의 캐시 차감
		Cash userCash = cashRepository.findByUuid(request.getUserUuid())
			.orElseGet(() -> Cash.builder()
				.uuid(request.getUserUuid())
				.balance(0.0)
				.build());

		if (userCash.getBalance() < request.getAmount()) {
			throw new RuntimeException("유저의 캐시 잔액이 부족합니다.");
		}

		Cash updatedUserCash = Cash.builder()
			.id(userCash.getId())
			.uuid(request.getUserUuid())
			.balance(userCash.getBalance() - request.getAmount())
			.build();
		cashRepository.save(updatedUserCash);

		// 어드민 계좌로 입금
		AdminCash adminCash = adminCashRepository.findById(1L)
			.orElseGet(() -> AdminCash.builder()
				.balance(0.0)
				.build());

		adminCash.addBalance(request.getAmount());
		adminCashRepository.save(adminCash);

		// CashTransfer 생성
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(request.getUserUuid())
			.partnerUuid(request.getPartnerUuid())
			.amount(request.getAmount())
			.status(CashTransferStatus.COMPLETED)
			.build();
		cashTransferRepository.save(cashTransfer);

		PaymentCompletedEvent event = PaymentCompletedEvent.builder()
			.requestId(request.getRequestId())
			.partnerId(request.getPartnerUuid())
			.build();

		kafkaTemplate.send("payment-completed", event);
	}

	@Override
	@Transactional
	public void cancelPayment(String userUuid, String partnerUuid, Double amount) {
		// 어드민 계좌에서 금액 차감
		AdminCash adminCash = adminCashRepository.findById(1L)
			.orElseThrow(() -> new RuntimeException("어드민 캐시 정보를 찾을 수 없습니다."));

		adminCash.subtractBalance(amount);
		adminCashRepository.save(adminCash);

		// 유저의 캐시에 금액 추가
		Cash userCash = cashRepository.findByUuid(userUuid)
			.orElseThrow(() -> new RuntimeException("유저의 캐시 정보를 찾을 수 없습니다."));

		Cash updatedUserCash = Cash.builder()
			.id(userCash.getId())
			.uuid(userUuid)
			.balance(userCash.getBalance() + amount)
			.build();
		cashRepository.save(updatedUserCash);

		// CashTransfer 생성 (취소 상태로)
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(userUuid)
			.partnerUuid(partnerUuid)
			.amount(amount)
			.status(CashTransferStatus.CANCELLED)
			.build();
		cashTransferRepository.save(cashTransfer);
	}

}