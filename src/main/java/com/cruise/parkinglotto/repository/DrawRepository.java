package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Draw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrawRepository extends JpaRepository<Draw, Long> {
    Optional<Draw> findById(long drawId);
}
