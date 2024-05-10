package com.mfc.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfc.payment.domain.Cash;

public interface CashRepository extends JpaRepository<Cash, Long> {
}
