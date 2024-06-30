package com.mfc.payment.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.common.exception.BaseException;
import com.mfc.payment.common.response.BaseResponseStatus;
import com.mfc.payment.domain.AdminCash;
import com.mfc.payment.domain.Cash;
import com.mfc.payment.domain.CashTransfer;
import com.mfc.payment.dto.kafka.PaymentCompletedEvent;
import com.mfc.payment.dto.request.TransferRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.dto.response.CashTransferHistoryResponse;
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
		Cash cash = getCashByUuid(uuid);
		cash.addBalance(amount);
		cashRepository.save(cash);
		log.info("Cash updated for user {}: new balance {}", uuid, cash.getBalance());
	}

	@Override
	@Transactional
	public CashResponse getCashBalance(String uuid) {
		Cash cash = getCashByUuid(uuid);
		return CashResponse.builder().balance(cash.getBalance()).build();
	}

	@Override
	@Transactional
	public void consumeUserSettlement(TransferRequest request) {
		validateTransferRequest(request);
		deductUserCash(request.getUserUuid(), request.getAmount());
		depositToAdminCash(request.getAmount());
		createCashTransfer(request);
		sendPaymentCompletedEvent(request);
		log.info("User settlement consumed: {}", request);
	}

	@Override
	@Transactional
	public void cancelPayment(String userUuid, String partnerUuid) {
		CashTransfer lastTransfer = getLastTransfer(userUuid, partnerUuid);
		validateCancellation(lastTransfer);

		Double amount = lastTransfer.getAmount();
		deductAdminCash(amount);
		refundUserCash(userUuid, amount);
		createCashTransfer(userUuid, partnerUuid, amount);
		log.info("Payment cancelled: user {}, partner {}, amount {}", userUuid, partnerUuid, amount);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CashTransferHistoryResponse> getCashTransferHistory(String uuid, CashTransferStatus status, LocalDate month, Pageable pageable) {
		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;

		if (month != null) {
			startDateTime = month.atStartOfDay();
			endDateTime = month.plusMonths(1).atStartOfDay().minusNanos(1);
		}

		return cashTransferRepository.findByCashTransferHistory(uuid, status, startDateTime, endDateTime, pageable)
			.map(this::mapToCashTransferHistoryResponse);
	}

	private Cash getCashByUuid(String uuid) {
		return cashRepository.findByUuid(uuid)
			.orElseGet(() -> createNewCash(uuid));
	}

	private Cash createNewCash(String uuid) {
		Cash newCash = Cash.builder().uuid(uuid).balance(0.0).build();
		return cashRepository.save(newCash);
	}

	private void deductUserCash(String userUuid, Double amount) {
		Cash userCash = getCashByUuid(userUuid);
		if (userCash.getBalance() < amount) {
			throw new BaseException(BaseResponseStatus.NOT_ENOUGH_CASH);
		}
		userCash.subtractBalance(amount);
		cashRepository.save(userCash);
	}

	private void depositToAdminCash(Double amount) {
		AdminCash adminCash = getAdminCash();
		adminCash.addBalance(amount);
		adminCashRepository.save(adminCash);
	}

	private void createCashTransfer(TransferRequest request) {
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(request.getUserUuid())
			.partnerUuid(request.getPartnerUuid())
			.amount(request.getAmount())
			.status(CashTransferStatus.PAYMENT_COMPLETED)
			.build();
		cashTransferRepository.save(cashTransfer);
	}

	private void createCashTransfer(String userUuid, String partnerUuid, Double amount) {
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(userUuid)
			.partnerUuid(partnerUuid)
			.amount(amount)
			.status(CashTransferStatus.CANCELLED)
			.build();
		cashTransferRepository.save(cashTransfer);
	}

	private void sendPaymentCompletedEvent(TransferRequest request) {
		PaymentCompletedEvent event = PaymentCompletedEvent.builder()
			.requestId(request.getRequestId())
			.partnerId(request.getPartnerUuid())
			.build();
		log.info("send message {}, {}",event.getPartnerId(), event.getRequestId());
		kafkaTemplate.send("payment-completed", event);
	}

	private void deductAdminCash(Double amount) {
		AdminCash adminCash = getAdminCash();
		if (adminCash.getBalance() < amount) {
			throw new BaseException(BaseResponseStatus.NOT_ENOUGH_ADMIN_CASH);
		}
		adminCash.subtractBalance(amount);
		adminCashRepository.save(adminCash);
	}

	private void refundUserCash(String userUuid, Double amount) {
		Cash userCash = getCashByUuid(userUuid);
		userCash.addBalance(amount);
		cashRepository.save(userCash);
	}

	private AdminCash getAdminCash() {
		return adminCashRepository.findById(1L)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.ADMIN_CASH_NOT_FOUND));
	}

	private CashTransferHistoryResponse mapToCashTransferHistoryResponse(CashTransfer transfer) {
		return CashTransferHistoryResponse.builder()
			.id(transfer.getId())
			.userUuid(transfer.getUserUuid())
			.partnerUuid(transfer.getPartnerUuid())
			.amount(transfer.getAmount())
			.status(transfer.getStatus())
			.createdAt(transfer.getCreatedDate())
			.build();
	}

	private void validateTransferRequest(TransferRequest request) {
		if (request == null || request.getAmount() <= 0) {
			throw new BaseException(BaseResponseStatus.INVALID_TRANSFER_REQUEST);
		}
	}

	private CashTransfer getLastTransfer(String userUuid, String partnerUuid) {
		return cashTransferRepository.findFirstByUserUuidAndPartnerUuidOrderByCreatedDateDesc(userUuid, partnerUuid)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.CASH_TRANSFER_NOT_FOUND));
	}

	private void validateCancellation(CashTransfer lastTransfer) {
		if (lastTransfer.getStatus() == CashTransferStatus.CANCELLED) {
			throw new BaseException(BaseResponseStatus.ALREADY_CANCELLED);
		}
	}
}