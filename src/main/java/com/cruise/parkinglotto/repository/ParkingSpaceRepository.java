package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.ParkingSpace;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    Optional<ParkingSpace> findByIdAndDrawId(long id, long drawId);
    List<ParkingSpace> findByDrawId(Long drawId);

    @Modifying
    @Transactional
    @Query("UPDATE ParkingSpace p SET p.remainSlots = p.remainSlots - 1 WHERE p.id = :id AND p.remainSlots > 0")
    void decrementRemainSlots(@Param("id") Long id);
}
