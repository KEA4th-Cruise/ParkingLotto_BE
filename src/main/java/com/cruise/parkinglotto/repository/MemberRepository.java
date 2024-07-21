package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Modifying
    @Query("UPDATE Member m SET m.recentLossCount = 0 WHERE m.id = :memberId")
    void resetRecentLossCount(@Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE Member m SET m.recentLossCount = m.recentLossCount + 1 WHERE m.id = :memberId")
    void increaseRecentLossCount(@Param("memberId") Long memberId);
}
