package com.cruise.parkinglotto.repository;
import com.cruise.parkinglotto.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    Optional<ParkingSpace> findByIdAndDraw_Id(long id, long drawId);
}
