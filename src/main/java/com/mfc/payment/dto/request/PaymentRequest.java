package com.mfc.payment.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
	private Long paymentId;
	private String paymentStatus;
	private Integer amount;
	private LocalDateTime paymentDate;
}
