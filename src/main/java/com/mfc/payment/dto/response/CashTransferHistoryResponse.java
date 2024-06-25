package com.mfc.payment.dto.response;

import java.time.LocalDateTime;

import com.mfc.payment.common.CashTransferStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CashTransferHistoryResponse {
	private Long id;
	private String userUuid;
	private String partnerUuid;
	private Double amount;
	private CashTransferStatus status;
	private LocalDateTime createdAt;
}