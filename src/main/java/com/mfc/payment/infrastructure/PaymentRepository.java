package com.mfc.payment.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfc.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByUuid(UUID uuid);
}
