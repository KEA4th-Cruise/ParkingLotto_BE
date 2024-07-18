package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
}
