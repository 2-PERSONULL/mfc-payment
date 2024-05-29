package com.mfc.payment.domain;

import java.time.LocalDateTime;

import com.mfc.payment.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class Payment extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@Column(name = "payment_id", nullable = false)
	private Long paymentId;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@Column(name = "payment_status", nullable = false)
	private String paymentStatus;

	@Column(name = "payment_date", nullable = false)
	private LocalDateTime paymentDate;

	@Builder
	public Payment(String uuid, Long paymentId, Double amount, String paymentStatus, LocalDateTime paymentDate) {
		this.uuid = uuid;
		this.paymentId = paymentId;
		this.amount = amount;
		this.paymentStatus = paymentStatus;
		this.paymentDate = paymentDate;
	}
}
