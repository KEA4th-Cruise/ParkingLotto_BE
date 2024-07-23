package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.WeightDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface WeightDetailsRepository extends JpaRepository<WeightDetails, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE WeightDetails m SET m.recentLossCount = 0 WHERE m.member = :memberId")
    void resetRecentLossCount(@Param("memberId") Long memberId);

    @Modifying
    @Transactional
    @Query("UPDATE WeightDetails m SET m.recentLossCount = m.recentLossCount + 1 WHERE m.member = :memberId")
    void increaseRecentLossCount(@Param("memberId") Long memberId);
}
