package com.mfc.payment.presentation;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mfc.payment.application.CashService;
import com.mfc.payment.application.PaymentService;
import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.common.response.BaseResponse;
import com.mfc.payment.dto.request.PaymentRequest;
import com.mfc.payment.dto.request.TransferRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.dto.response.CashTransferHistoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cash")
@Tag(name = "캐시 관리", description = "캐시 관련 작업을 위한 API")
public class CashController {

	private final CashService cashService;
	private final PaymentService paymentService;

	@Operation(summary = "캐시 충전", description = "사용자 계정에 캐시를 충전합니다")
	@ApiResponse(responseCode = "200", description = "충전 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 입력")
	@PutMapping("/charge")
	public BaseResponse<Void> chargeCash(
		@Parameter(description = "결제 요청 상세 정보", required = true) @RequestBody PaymentRequest request,
		@Parameter(description = "사용자 UUID", required = true) @RequestHeader String uuid) {
		paymentService.chargeCash(request, uuid);
		return new BaseResponse<>();
	}

	@Operation(summary = "캐시 잔액 조회", description = "사용자의 현재 캐시 잔액을 조회합니다")
	@ApiResponse(responseCode = "200", description = "잔액 조회 성공",
		content = @Content(schema = @Schema(implementation = CashResponse.class)))
	@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
	@GetMapping("/balance")
	public BaseResponse<CashResponse> getCashBalance(
		@Parameter(description = "사용자 UUID", required = true) @RequestHeader String uuid) {
		final CashResponse cashResponse = cashService.getCashBalance(uuid);
		return new BaseResponse<>(cashResponse);
	}

	@Operation(summary = "결제 취소", description = "이전에 진행한 캐시 결제를 취소합니다")
	@ApiResponse(responseCode = "200", description = "결제 취소 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 입력")
	@ApiResponse(responseCode = "404", description = "결제 내역을 찾을 수 없음")
	@PutMapping("/cancel")
	public BaseResponse<Void> cancelPayment(
		@Parameter(description = "이체 요청 상세 정보", required = true) @RequestBody TransferRequest request) {
		cashService.cancelPayment(request.getUserUuid(), request.getPartnerUuid(), request.getAmount());
		return new BaseResponse<>();
	}

	@Operation(summary = "캐시 이체", description = "사용자로부터 파트너에게 캐시를 이체합니다")
	@ApiResponse(responseCode = "200", description = "이체 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 입력")
	@ApiResponse(responseCode = "402", description = "잔액 부족")
	@PutMapping("/transfer")
	public BaseResponse<Void> transferCash(
		@Parameter(description = "이체 요청 상세 정보", required = true) @RequestBody TransferRequest request) {
		cashService.consumeUserSettlement(request);
		return new BaseResponse<>();
	}

	@Operation(summary = "캐시 이체 내역 조회",
		description = "상태와 월별로 필터링하여 페이지네이션된 캐시 이체 내역을 조회합니다")
	@ApiResponse(responseCode = "200", description = "이체 내역 조회 성공",
		content = @Content(schema = @Schema(implementation = Page.class)))
	@ApiResponse(responseCode = "400", description = "잘못된 입력")
	@GetMapping("/history")
	public BaseResponse<Page<CashTransferHistoryResponse>> getCashTransferHistory(
		@Parameter(description = "사용자 UUID", required = true) @RequestHeader String uuid,
		@Parameter(description = "상태별 필터", schema = @Schema(allowableValues = {"PAYMENT_COMPLETED", "SETTLEMENT_COMPLETED", "CANCELLED"}))
		@RequestParam(required = false) CashTransferStatus status,
		@Parameter(description = "월별 필터 (형식: yyyy-MM)")
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") LocalDate month,
		@Parameter(description = "페이지네이션 정보") Pageable pageable) {
		Page<CashTransferHistoryResponse> history = cashService.getCashTransferHistory(uuid, status, month, pageable);
		return new BaseResponse<>(history);
	}
}