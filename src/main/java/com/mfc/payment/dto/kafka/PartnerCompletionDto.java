package com.mfc.payment.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerCompletionDto {
	private String userUuid;
	private String partnerUuid;
	private Double amount;
	private Long tradeId;

}
