package com.mfc.payment.domain;

import java.time.LocalDateTime;

import com.mfc.payment.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Payment extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Column(name = "payment_method", nullable = false)
	private String paymentMethod;

	@Column(name = "payment_status", nullable = false)
	private String paymentStatus;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "payment_date", nullable = false)
	private LocalDateTime paymentDate;

	@Builder
	public Payment(Long userId, Long orderId, Integer amount, String paymentMethod, String paymentStatus,
		String transactionId, LocalDateTime paymentDate) {
		this.userId = userId;
		this.orderId = orderId;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.transactionId = transactionId;
		this.paymentDate = paymentDate;
	}
}
