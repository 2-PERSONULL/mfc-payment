package com.mfc.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CashResponse {
	private Integer balance;
}
