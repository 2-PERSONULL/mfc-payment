package com.mfc.payment.presentation;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mfc.payment.application.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
	private final PaymentService paymentService;
	private IamportClient iamportClient;

	@Value("${iamport.key}")
	private String apiKey;

	@Value("${imp.secret}")
	private String secretKey;

	@PostConstruct
	public void init() {
		this.iamportClient = new IamportClient(apiKey, secretKey);
	}

	@PostMapping("/payment/{imp_uid}")
	public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid)
		throws IamportResponseException, IOException {
		return iamportClient.paymentByImpUid(imp_uid);
	}
	/**
	 * 결제내역 조회
	 * @param memberId
	 * @return
	 */
	// @GetMapping("/payment-history/{memberId}")
	// public ResponseEntity<List<PaymentHistoryDto>> paymentList(@PathVariable Long memberId) {
	// 	return ResponseEntity.status(HttpStatus.OK).body(paymentService.paymentHistoryList(memberId));
	// }
}