package com.tickethub.concert.infrastructure.repository;

import com.tickethub.concert.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
