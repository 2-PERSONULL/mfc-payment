package com.mfc.payment.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
	private Integer amount;
	private String paymentStatus;
	private LocalDateTime paymentDate;
}
