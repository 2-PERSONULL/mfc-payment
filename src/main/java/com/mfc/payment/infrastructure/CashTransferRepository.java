package com.mfc.payment.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.domain.CashTransfer;

public interface CashTransferRepository extends JpaRepository<CashTransfer, Long> {
	Optional<CashTransfer> findByPartnerUuidAndCashTransferStatus(String partnerUuid, CashTransferStatus status);
}
