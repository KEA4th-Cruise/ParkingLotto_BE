package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);

    @Query("SELECT m.enrollmentStatus FROM Member m WHERE m.id = :memberId")
    EnrollmentStatus findEnrollmentStatusById(@Param("memberId") Long memberId);

    Optional<Member> findById(Long memberId);

    @Modifying
    @Query("UPDATE Member m SET m.enrollmentStatus = com.cruise.parkinglotto.domain.enums.EnrollmentStatus.PENDING WHERE m.accountId = :accountId")
    int updateEnrollmentStatusToPending(@Param("accountId") String accountId);
}
