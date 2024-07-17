package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DrawRepository extends JpaRepository<Draw, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Draw d SET d.seedNum = :seedNum WHERE d.id = :drawId")
    void updateSeedNum(@Param("drawId") Long drawId, @Param("seedNum") String seedNum);
}