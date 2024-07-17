package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Draw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawRepository extends JpaRepository<Draw,Long> {
}
