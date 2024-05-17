package com.mfc.payment.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mfc.payment.application.CashService;
import com.mfc.payment.application.PaymentService;
import com.mfc.payment.common.response.BaseResponse;
import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.response.CashResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cash")
public class CashController {

	private final CashService cashService;
	private final PaymentService paymentService;

	@PostMapping("/charge")
	public BaseResponse<CashResponse> chargeCash(@RequestBody PaymentRequest request, @RequestHeader UUID uuid) {
		final CashResponse cashResponse = paymentService.chargeCash(request, uuid);
		return new BaseResponse<>(cashResponse);
	}

	@GetMapping("/balance")
	public BaseResponse<CashResponse> getCashBalance(@RequestHeader UUID uuid) {
		final CashResponse cashResponse = cashService.getCashBalance(uuid);
		return new BaseResponse<>(cashResponse);
	}
}
