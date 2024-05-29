package com.mfc.payment.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.domain.AdminCash;
import com.mfc.payment.domain.Cash;
import com.mfc.payment.domain.CashTransfer;
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

	@KafkaListener(topics = "user-settlement", groupId = "settlement-group")
	@Override
	@Transactional
	public void consumeUserSettlement(String message) {
		log.info("Received user settlement message: {}", message);

		// 메시지를 파싱하여 필요한 정보 추출
		// 예시: "UserUuid: user123, Amount: 1000"
		String[] parts = message.split(", ");
		String userUuid = parts[0].split(": ")[1];
		Double amount = Double.parseDouble(parts[1].split(": ")[1]);

		// 유저의 캐시 차감
		Cash userCash = cashRepository.findByUuid(userUuid)
			.orElseThrow(() -> new RuntimeException("유저의 캐시 정보를 찾을 수 없습니다."));

		Cash updatedUserCash = Cash.builder()
			.id(userCash.getId())
			.uuid(userUuid)
			.balance(userCash.getBalance() - amount)
			.build();
		cashRepository.save(updatedUserCash);

		// 어드민 계좌로 입금
		AdminCash adminCash = adminCashRepository.findById(1L)
			.orElseGet(() -> AdminCash.builder()
				.balance(0.0)
				.build());

		adminCash.addBalance(amount);

		// CashTransfer 생성
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(userUuid)
			.amount(amount)
			.status(CashTransferStatus.COMPLETED)
			.build();
		cashTransferRepository.save(cashTransfer);
	}

	@KafkaListener(topics = "partner-completion", groupId = "completion-group")
	@Override
	@Transactional
	public void consumePartnerCompletion(String message) {
		log.info("Received partner completion message: {}", message);

		// 메시지를 파싱하여 필요한 정보 추출
		// 예시: "PartnerUuid: partner123, Amount: 800"
		String[] parts = message.split(", ");
		String partnerUuid = parts[0].split(": ")[1];
		Double amount = Double.parseDouble(parts[1].split(": ")[1]);

		// 어드민 계좌에서 차감
		AdminCash adminAccount = adminCashRepository.findById(1L)
			.orElseGet(() -> AdminCash.builder()
				.balance(0.0)
				.build());

		adminAccount.subtractBalance(amount);

		// 파트너 계좌로 입금
		Cash partnerCash = cashRepository.findByUuid(partnerUuid)
			.map(existingCash -> Cash.builder()
				.id(existingCash.getId())
				.uuid(partnerUuid)
				.balance(existingCash.getBalance() + amount)
				.build())
			.orElseGet(() -> Cash.builder()
				.uuid(partnerUuid)
				.balance(amount)
				.build());
		cashRepository.save(partnerCash);

		// CashTransfer 생성
		CashTransfer cashTransfer = CashTransfer.builder()
			.partnerUuid(partnerUuid)
			.amount(amount)
			.status(CashTransferStatus.COMPLETED)
			.build();
		cashTransferRepository.save(cashTransfer);
	}
}