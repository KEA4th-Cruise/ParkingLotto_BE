package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.WeightDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface WeightDetailsRepository extends JpaRepository<WeightDetails, Long> {
    @Modifying
    @Query("UPDATE WeightDetails m SET m.recentLossCount = 0 WHERE m.member = :member")
    void resetRecentLossCount(@Param("member") Member member);

    @Modifying
    @Query("UPDATE WeightDetails m SET m.recentLossCount = m.recentLossCount + 1 WHERE m.member = :member")
    void increaseRecentLossCount(@Param("member") Member member);

    Optional<WeightDetails> findOptionalByMemberId(Long memberId);

    @Modifying
    @Transactional
    @Query("UPDATE WeightDetails w SET w.address = :address WHERE w.member = :member")
    void updateAddress(@Param("member") Member member, @Param("address") String address);

    WeightDetails findByMemberId(Long memberId);

}
