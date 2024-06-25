package com.mfc.payment.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfc.payment.domain.CashTransfer;

public interface CashTransferRepository extends JpaRepository<CashTransfer, Long> {
	List<CashTransfer> findByUserUuid(String uuid);
}
