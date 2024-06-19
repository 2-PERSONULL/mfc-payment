package com.mfc.payment.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class TradeSettledEventDto {
	private String userUuid;
	private String partnerUuid;
	private Double amount;
	private Long tradeId;

}
