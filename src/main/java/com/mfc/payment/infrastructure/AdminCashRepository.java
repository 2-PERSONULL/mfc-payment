package com.mfc.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfc.payment.domain.AdminCash;

public interface AdminCashRepository extends JpaRepository<AdminCash, Long> {
}
