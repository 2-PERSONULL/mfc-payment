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
	public void createOrUpdateCash(String uuid, Integer amount) {
		Cash cash = cashRepository.findByUuid(uuid)
			.map(existingCash -> Cash.builder()
				.id(existingCash.getId())
				.uuid(uuid)
				.balance(existingCash.getBalance() + amount)
				.build())
			.orElseGet(() -> Cash.builder()
				.uuid(uuid)
				.balance(amount)
				.build());

		cashRepository.save(cash);

	}

	@Transactional(readOnly = true)
	public CashResponse getCashBalance(String uuid) {
		return cashRepository.findByUuid(uuid)
			.map(cash -> CashResponse.builder()
				.balance(cash.getBalance())
				.build())
			.orElseGet(() -> CashResponse.builder()
				.balance(0)
				.build());
	}
}