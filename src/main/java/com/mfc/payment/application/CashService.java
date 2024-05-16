package com.mfc.payment.application;

import com.mfc.payment.dto.response.CashResponse;

public interface CashService {

	CashResponse createOrUpdateCash(Long userId, Integer amount);

	CashResponse getCashBalance(Long userId);
}
