package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);

    Optional<Member> findById(Long memberId);

    @Query("select m.id from Member m where m.accountId = :accountId")
    Optional<Long> findIdByAccountId(@Param("accountId") String accountId);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.carNum = :carNum WHERE m.id = :memberId")
    void updateCarNum(@Param("memberId") Long memberId, @Param("carNum") String carNum);
  
    @Modifying
    @Query("UPDATE Member m SET m.enrollmentStatus = com.cruise.parkinglotto.domain.enums.EnrollmentStatus.PENDING WHERE m.accountId = :accountId")
    int updateEnrollmentStatusToPending(@Param("accountId") String accountId);

    @Modifying
    @Query("UPDATE Member m SET m.enrollmentStatus = com.cruise.parkinglotto.domain.enums.EnrollmentStatus.PREPENDING WHERE m.accountId = :accountId")
    int updateEnrollmentStatusToPrepending(@Param("accountId") String accountId);
  
    @Modifying
    @Query("UPDATE Member m SET m.enrollmentStatus = com.cruise.parkinglotto.domain.enums.EnrollmentStatus.ENROLLED WHERE m.accountId = :accountId")
    int updateEnrollmentStatusToEnrolled(@Param("accountId") String accountId);

    @Query("SELECT m FROM Member m WHERE m.enrollmentStatus = :enrollmentStatus")
    Page<Member> findByEnrollmentStatus(PageRequest pageRequest, @Param("enrollmentStatus") EnrollmentStatus enrollmentStatus);

    Optional<Member> findByAccountIdAndEnrollmentStatus(String accountId, EnrollmentStatus enrollmentStatus);

    Optional<Member> findByEmployeeNoAndEnrollmentStatus(String employeeNo, EnrollmentStatus enrollmentStatus);


}
