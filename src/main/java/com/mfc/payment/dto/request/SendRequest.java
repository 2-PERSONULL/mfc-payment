package com.mfc.payment.dto.request;

import lombok.Getter;

@Getter
public class SendRequest {
	private String toUuid;
	private Integer amount;
}
