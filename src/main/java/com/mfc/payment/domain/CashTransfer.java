package com.mfc.payment.domain;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.common.entity.BaseCreateTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cash_transfer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashTransfer extends BaseCreateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userUuid;

	private String partnerUuid;

	private Double amount;

	@Enumerated(EnumType.STRING)
	private CashTransferStatus status;

	@Builder
	public CashTransfer(String userUuid, String partnerUuid, Double amount, CashTransferStatus status) {
		this.userUuid = userUuid;
		this.partnerUuid = partnerUuid;
		this.amount = amount;
		this.status = status;
	}

}