package com.mfc.payment.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferRequest {
	private String requestId;
	private String userUuid;
	private String partnerUuid;
	private Double amount;
}