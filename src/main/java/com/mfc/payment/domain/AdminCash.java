package com.mfc.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_cash")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminCash {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "balance")
	private Double balance;

	@Builder
	public AdminCash(Double balance) {
		this.balance = balance;
	}

	public void addBalance(Double amount) {
		this.balance += amount;
	}

	public void subtractBalance(Double amount) {
		this.balance -= amount;
	}
}