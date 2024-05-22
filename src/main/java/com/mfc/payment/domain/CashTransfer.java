package com.mfc.payment.domain;

import com.mfc.payment.common.entity.BaseCreateTimeEntity;
import com.mfc.payment.common.CashTransferStatus;

import jakarta.persistence.Column;
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
@Table(name = "pending_payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashTransfer extends BaseCreateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_uuid")
	private String userUuid;

	@Column(name = "partner_uuid")
	private String partnerUuid;

	private Integer amount;

	@Enumerated(EnumType.STRING)
	private CashTransferStatus cashTransferStatus;

	@Builder
	public CashTransfer(Long id, String userUuid, String partnerUuid, Integer amount, CashTransferStatus cashTransferStatus) {
		this.id = id;
		this.userUuid = userUuid;
		this.partnerUuid = partnerUuid;
		this.amount = amount;
		this.cashTransferStatus = cashTransferStatus;
	}

	public void complete() {
		this.cashTransferStatus = CashTransferStatus.COMPLETED;
	}
}