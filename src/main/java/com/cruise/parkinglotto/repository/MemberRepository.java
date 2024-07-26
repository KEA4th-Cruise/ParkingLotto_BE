package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);

    @Query("SELECT m.enrollmentStatus FROM Member m WHERE m.id = :memberId")
    EnrollmentStatus findEnrollmentStatusById(@Param("memberId") Long memberId);

    Optional<Member> findById(Long memberId);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.carNum = :carNum WHERE m.id = :memberId")
    void updateCarNum(@Param("memberId") Long memberId, @Param("carNum") String carNum);
}
