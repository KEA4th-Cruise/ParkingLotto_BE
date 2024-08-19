package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.ParkingSpace;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    List<ParkingSpace> findByDrawId(Long drawId);

    @Modifying
    @Query("UPDATE ParkingSpace p SET p.remainSlots = p.remainSlots - 1 WHERE p.id = :id AND p.remainSlots > 0")
    void decrementRemainSlots(@Param("id") Long id);

    List<ParkingSpace> findByConfirmed(boolean confirmed);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ParkingSpace p WHERE p.id = :firstChoice AND p.draw.id = :drawId")
    ParkingSpace findUserCountWithDrawAndFirstChoice(
            @Param("firstChoice") Long firstChoice,
            @Param("drawId") Long drawId
    );
}
