package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.repository.querydsl.ApplicantCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long>, ApplicantCustomRepository {

    @Query("select a.parkingSpaceId from Applicant a where a.id =:applicantId")
    Optional<Long> findParkingSpaceById(@Param("applicantId") Long applicantId);

    @Query("SELECT a FROM Applicant a WHERE a.member.id = :memberId AND a.draw.id = :drawId ")
    Optional<Applicant> findApplicantByMemberIdAndDrawId(@Param("memberId") Long memberId, @Param("drawId") Long drawId);

    List<Applicant> findByDrawId(Long drawId);

    @Modifying
    @Query("UPDATE Applicant a SET a.parkingSpaceId = :parkingSpaceId WHERE a.id = :winnerId")
    void updateParkingSpaceId(@Param("winnerId") Long winnerId, @Param("parkingSpaceId") Long parkingSpaceId);

    Page<Applicant> findByDrawId(PageRequest pageRequest, Long drawId);

    Optional<Applicant> findById(Long applicantId);

    @Query("SELECT COALESCE(MAX(a.userSeedIndex), 0) FROM Applicant a WHERE a.draw = :draw")
    Integer findMaxUserSeedIndexByDraw(@Param("draw") Draw draw);

    Optional<Applicant> findByDrawIdAndMemberId(Long drawId, Long memberId);

    @Query("SELECT COUNT(a) FROM Applicant a WHERE a.draw.id = :drawId AND a.firstChoice = :firstChoice")
    Integer countByDrawIdAndFirstChoice(@Param("drawId") Long drawId, @Param("firstChoice") Long firstChoice);

    @Query("SELECT COUNT(a) FROM Applicant a WHERE a.draw.id = :drawId")
    Integer countByDrawId(@Param("drawId") Long drawId);

    Applicant findByDrawIdAndId(Long drawId, Long winnerId);

    Applicant findByDrawIdAndReserveNum(Long drawId, Integer reserveNum);

    List<Applicant> findByDrawIdAndReserveNumGreaterThan(Long drawId, Integer reserveNum);

    @Transactional
    void deleteByDrawIdAndMember(Long drawId, Member member);

    @Query("SELECT a FROM Applicant a WHERE a.member.id = :memberId")
    List<Applicant> findByMemberId(@Param("memberId") Long memberId);
}
