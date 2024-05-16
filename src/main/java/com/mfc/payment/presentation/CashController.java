package com.mfc.payment.presentation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mfc.payment.common.response.BaseResponse;
import com.mfc.payment.dto.request.CashRequest;
import com.mfc.payment.application.CashService;
import com.mfc.payment.dto.response.CashResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cash")
public class CashController {

	private final CashService cashService;

	@PostMapping
	public BaseResponse<CashResponse> createOrUpdateCash(@RequestBody CashRequest request) {
		final CashResponse cashResponse = cashService.createOrUpdateCash(request.getUserId(), request.getAmount());
		return new BaseResponse<>(cashResponse);
	}

	@GetMapping("/{userId}")
	public BaseResponse<CashResponse> getCashBalance(@PathVariable Long userId) {
		final CashResponse cashResponse = cashService.getCashBalance(userId);
		return new BaseResponse<>(cashResponse);
	}
}
