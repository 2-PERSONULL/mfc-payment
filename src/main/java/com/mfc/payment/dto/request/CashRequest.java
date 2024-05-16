package com.mfc.payment.dto.request;

import lombok.Getter;

@Getter
public class CashRequest {
	private Long userId;
	private Integer amount;
}
