package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);

    @Query("SELECT m.enrollmentStatus FROM Member m WHERE m.id = :memberId")
    EnrollmentStatus findEnrollmentStatusById(@Param("memberId") Long memberId);

    Optional<Member> findById(Long memberId);


    @Query("select m.id from Member m where m.accountId = :accountId")
    Optional<Long> findIdByAccountId(@Param("accountId") String accountId);

    @Modifying
    @Query("UPDATE Member m SET m.enrollmentStatus = com.cruise.parkinglotto.domain.enums.EnrollmentStatus.PENDING WHERE m.accountId = :accountId")
    int updateEnrollmentStatusToPending(@Param("accountId") String accountId);

    @Modifying
    @Query("UPDATE Member m SET m.enrollmentStatus = null WHERE m.accountId = :accountId")
    int updateEnrollmentStatusToNull(@Param("accountId") String accountId);
  
    @Modifying
    @Query("UPDATE Member m SET m.enrollmentStatus = com.cruise.parkinglotto.domain.enums.EnrollmentStatus.ENROLLED WHERE m.accountId = :accountId")
    int updateEnrollmentStatusToEnrolled(@Param("accountId") String accountId);

    @Query("SELECT m FROM Member m WHERE m.enrollmentStatus = :enrollmentStatus")
    List<Member> findByEnrollmentStatus(@Param("enrollmentStatus") EnrollmentStatus enrollmentStatus);


}
