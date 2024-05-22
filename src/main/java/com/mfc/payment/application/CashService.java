package com.mfc.payment.application;

import com.mfc.payment.domain.event.PartnerCompletionEvent;
import com.mfc.payment.dto.response.CashResponse;

public interface CashService {

	void createOrUpdateCash(String uuid, Integer amount);

	CashResponse getCashBalance(String uuid);

	void processPartnerSettlement(String userUuid, String partnerUuid, Integer amount);

	void handlePartnerCompletionEvent(PartnerCompletionEvent event);

}
