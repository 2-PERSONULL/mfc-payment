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
public class Cash extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false, unique = true)
	private Long userId;

	@Column(name = "cash_balance", nullable = false)
	private Integer balance;

	@Builder
	public Cash(Long userId, Integer balance) {
		this.userId = userId;
		this.balance = balance;
	}
}
