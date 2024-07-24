package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);

    @Query("SELECT m.enrollmentStatus FROM Member m WHERE m.id = :memberId")
    EnrollmentStatus findEnrollmentStatusById(@Param("memberId") Long memberId);

    @Query("select m.id from Member m where m.accountId = :accountId")
    Optional<Long> findIdByAccountId(@Param("accountId") String accountId);
}
