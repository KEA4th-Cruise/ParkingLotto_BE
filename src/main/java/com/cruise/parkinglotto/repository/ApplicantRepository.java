package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public interface ApplicantRepository extends JpaRepository<Applicant,Long> {

   @Query("select a.id from Applicant a where a.member.id = :memberId")
   Optional<Long> findByMember(@Param("memberId") Long memberId);

   @Query("select a.parkingSpaceId from Applicant a where a.id =:applicantId")
   Optional<Long> findParkingSpaceId(@Param("applicantId") Long applicantId);
  
   List<Applicant> findByDrawId(Long drawId);

}
