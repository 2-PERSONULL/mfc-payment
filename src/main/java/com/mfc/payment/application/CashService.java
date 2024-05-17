package com.mfc.payment.application;

import java.util.UUID;

import com.mfc.payment.dto.response.CashResponse;

public interface CashService {

	CashResponse createOrUpdateCash(UUID uuid, Integer amount);

	CashResponse getCashBalance(UUID uuid);
}
