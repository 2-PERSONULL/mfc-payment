package com.mfc.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.domain.Cash;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.infrastructure.CashRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CashServiceImpl implements CashService {

	private final CashRepository cashRepository;

	@Transactional
	public CashResponse createOrUpdateCash(Long userId, Integer amount) {
		Cash cash = cashRepository.findByUserId(userId)
			.map(existingCash -> Cash.builder()
				.balance(existingCash.getBalance() + amount)
				.build())
			.orElseGet(() -> Cash.builder()
				.userId(userId)
				.balance(amount)
				.build());

		Cash savedCash = cashRepository.save(cash);
		return CashResponse.builder()
			.userId(savedCash.getUserId())
			.balance(savedCash.getBalance())
			.build();
	}

	@Transactional(readOnly = true)
	public CashResponse getCashBalance(Long userId) {
		return cashRepository.findByUserId(userId)
			.map(cash -> CashResponse.builder()
				.userId(cash.getUserId())
				.balance(cash.getBalance())
				.build())
			.orElseGet(() -> CashResponse.builder()
				.userId(userId)
				.balance(0)
				.build());
	}
}