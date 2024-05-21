package com.mfc.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.payment.common.exception.BaseException;
import com.mfc.payment.common.response.BaseResponseStatus;
import com.mfc.payment.domain.Cash;
import com.mfc.payment.dto.request.SendRequest;
import com.mfc.payment.dto.response.CashResponse;
import com.mfc.payment.infrastructure.CashRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CashServiceImpl implements CashService {

	private final CashRepository cashRepository;

	@Override
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

	@Override
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

	@Override
	@Transactional
	public void sendCash(String uuid, SendRequest request) {
		Integer amount = request.getAmount();
		String toUuid = request.getToUuid();
		// 보내는 사람의 캐시 잔액 확인
		Cash fromCash = cashRepository.findByUuid(uuid)
			.orElseGet(() -> Cash.builder()
				.uuid(uuid)
				.balance(0)
				.build());

		if (fromCash.getBalance() < amount) {
			throw new BaseException(BaseResponseStatus.NOT_ENOUGH_CASH);
		}

		// 보내는 사람의 캐시 잔액 차감
		Cash updatedFromCash = Cash.builder()
			.id(fromCash.getId())
			.uuid(uuid)
			.balance(fromCash.getBalance() - amount)
			.build();
		cashRepository.save(updatedFromCash);

		// 받는 사람의 캐시 잔액 증가
		Cash toCash = cashRepository.findByUuid(toUuid)
			.map(existingCash -> Cash.builder()
				.id(existingCash.getId())
				.uuid(toUuid)
				.balance(existingCash.getBalance() + amount)
				.build())
			.orElseGet(() -> Cash.builder()
				.uuid(toUuid)
				.balance(amount)
				.build());
		cashRepository.save(toCash);
	}

}