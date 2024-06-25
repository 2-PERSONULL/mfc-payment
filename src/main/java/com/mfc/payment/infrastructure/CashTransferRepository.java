package com.mfc.payment.infrastructure;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mfc.payment.common.CashTransferStatus;
import com.mfc.payment.domain.CashTransfer;

public interface CashTransferRepository extends JpaRepository<CashTransfer, Long> {
	@Query("SELECT ct FROM CashTransfer ct WHERE " +
		"(ct.userUuid = :uuid OR ct.partnerUuid = :uuid) " +
		"AND (:status IS NULL OR ct.status = :status) " +
		"AND (:startDate IS NULL OR ct.createdDate >= :startDate) " +
		"AND (:endDate IS NULL OR ct.createdDate <= :endDate)")
	Page<CashTransfer> findByCashTransferHistory(
		@Param("uuid") String uuid,
		@Param("status") CashTransferStatus status,
		@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate,
		Pageable pageable
	);
}