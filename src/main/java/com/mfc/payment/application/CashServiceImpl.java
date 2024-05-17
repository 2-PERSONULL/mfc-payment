package com.mfc.payment.application;

import java.util.UUID;

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
	public CashResponse createOrUpdateCash(UUID uuid, Integer amount) {
		Cash cash = cashRepository.findByUuid(uuid)
			.map(existingCash -> Cash.builder()
				.uuid(existingCash.getUuid())
				.balance(existingCash.getBalance() + amount)
				.build())
			.orElseGet(() -> Cash.builder()
				.uuid(uuid)
				.balance(amount)
				.build());

		Cash savedCash = cashRepository.save(cash);
		return CashResponse.builder()
			.balance(savedCash.getBalance())
			.build();
	}

	@Transactional(readOnly = true)
	public CashResponse getCashBalance(UUID uuid) {
		return cashRepository.findByUuid(uuid)
			.map(cash -> CashResponse.builder()
				.balance(cash.getBalance())
				.build())
			.orElseGet(() -> CashResponse.builder()
				.balance(0)
				.build());
	}
}