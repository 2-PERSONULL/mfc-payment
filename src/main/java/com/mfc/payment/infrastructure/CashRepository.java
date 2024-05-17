package com.mfc.payment.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfc.payment.domain.Cash;

public interface CashRepository extends JpaRepository<Cash, Long> {
	Optional<Cash> findByUuid(UUID uuid);
}
