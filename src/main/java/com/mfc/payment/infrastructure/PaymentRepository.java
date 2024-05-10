package com.mfc.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfc.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
