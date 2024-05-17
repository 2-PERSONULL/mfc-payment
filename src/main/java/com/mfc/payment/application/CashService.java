package com.mfc.payment.application;

import com.mfc.payment.dto.response.CashResponse;

public interface CashService {

	void createOrUpdateCash(String uuid, Integer amount);

	CashResponse getCashBalance(String uuid);
}
