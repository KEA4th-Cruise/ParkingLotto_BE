package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DrawRepository extends JpaRepository<Draw, Long> {
    Optional<Draw> findById(long drawId);

    List<Draw> findByConfirmed(Boolean confirmed);

    @Modifying
    @Query("UPDATE Draw d SET d.status = :status WHERE d.id = :drawId")
    void updateStatus(@Param("drawId") Long drawId, @Param("status") DrawStatus status);

    Optional<Draw> findTopByStatusNotOrderByUsageStartAtDesc(DrawStatus status);

    List<Draw> findTop5ByTypeOrderByUsageStartAtDesc(DrawType type);

    List<Draw> findByYearAndType(String year, DrawType drawType);

    @Query("SELECT DISTINCT d.year FROM Draw d  ORDER BY d.year DESC")
    List<String> findYearList();

    List<Draw> findByStatus(DrawStatus status);
}

