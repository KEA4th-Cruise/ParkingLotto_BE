package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.recentLossCount = 0 WHERE m.id = :memberId")
    void resetRecentLossCount(@Param("memberId") Long memberId);
}
