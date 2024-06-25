package com.mfc.payment.application;

import java.util.List;
import java.util.stream.Collectors;

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
	}

	@Override
	@Transactional(readOnly = true)
	public CashResponse getCashBalance(String uuid) {
		Cash cash = getCashByUuid(uuid);
		return CashResponse.builder().balance(cash.getBalance()).build();
	}

	@Override
	@Transactional
	public void consumeUserSettlement(TransferRequest request) {
		deductUserCash(request.getUserUuid(), request.getAmount());
		depositToAdminCash(request.getAmount());
		createCashTransfer(request, CashTransferStatus.PAYMENT_COMPLETED);
		sendPaymentCompletedEvent(request);
	}

	@Override
	@Transactional
	public void cancelPayment(String userUuid, String partnerUuid, Double amount) {
		deductAdminCash(amount);
		refundUserCash(userUuid, amount);
		TransferRequest cancelRequest = createCancelRequest(userUuid, partnerUuid, amount);
		createCashTransfer(cancelRequest, CashTransferStatus.CANCELLED);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CashTransferHistoryResponse> getCashTransferHistory(String userUuid) {
		return cashTransferRepository.findByUserUuid(userUuid).stream()
			.map(this::mapToCashTransferHistoryResponse)
			.collect(Collectors.toList());
	}

	private Cash getCashByUuid(String uuid) {
		return cashRepository.findByUuid(uuid)
			.orElseGet(() -> createNewCash(uuid));
	}

	private Cash createNewCash(String uuid) {
		return Cash.builder().uuid(uuid).balance(0.0).build();
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

	private void createCashTransfer(TransferRequest request, CashTransferStatus status) {
		CashTransfer cashTransfer = CashTransfer.builder()
			.userUuid(request.getUserUuid())
			.partnerUuid(request.getPartnerUuid())
			.amount(request.getAmount())
			.status(status)
			.build();
		cashTransferRepository.save(cashTransfer);
	}

	private void sendPaymentCompletedEvent(TransferRequest request) {
		PaymentCompletedEvent event = PaymentCompletedEvent.builder()
			.requestId(request.getRequestId())
			.partnerId(request.getPartnerUuid())
			.build();
		kafkaTemplate.send("payment-completed", event);
	}

	private void deductAdminCash(Double amount) {
		AdminCash adminCash = getAdminCash();
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

	private TransferRequest createCancelRequest(String userUuid, String partnerUuid, Double amount) {
		return TransferRequest.builder()
			.userUuid(userUuid)
			.partnerUuid(partnerUuid)
			.amount(amount)
			.build();
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
}