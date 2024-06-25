package com.mfc.payment.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mfc.payment.application.CashService;
import com.mfc.payment.application.PaymentService;
import com.mfc.payment.common.response.BaseResponse;
import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.request.TransferRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.dto.response.CashTransferHistoryResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cash")
public class CashController {

	private final CashService cashService;
	private final PaymentService paymentService;

	@PutMapping("/charge")
	public BaseResponse<Void> chargeCash(@RequestBody PaymentRequest request, @RequestHeader String uuid) {
		paymentService.chargeCash(request, uuid);
		return new BaseResponse<>();
	}

	@GetMapping("/balance")
	public BaseResponse<CashResponse> getCashBalance(@RequestHeader String uuid) {
		final CashResponse cashResponse = cashService.getCashBalance(uuid);
		return new BaseResponse<>(cashResponse);
	}

	@PutMapping("/cancel")
	public BaseResponse<Void> cancelPayment(@RequestBody TransferRequest request) {
		cashService.cancelPayment(request.getUserUuid(), request.getPartnerUuid(), request.getAmount());
		return new BaseResponse<>();
	}

	@PutMapping("/transfer")
	public BaseResponse<Void> transferCash(@RequestBody TransferRequest request) {
		cashService.consumeUserSettlement(request);
		return new BaseResponse<>();
	}

	@GetMapping("/history")
	public BaseResponse<List<CashTransferHistoryResponse>> getCashTransferHistory(@RequestHeader String uuid) {
		List<CashTransferHistoryResponse> history = cashService.getCashTransferHistory(uuid);
		return new BaseResponse<>(history);
	}
}