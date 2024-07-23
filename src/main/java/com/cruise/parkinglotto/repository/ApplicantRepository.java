package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    @Query("select a.id from Applicant a where a.member.id = :memberId")
    Optional<Long> findByMember(@Param("memberId") Long memberId);

    @Query("select a.parkingSpaceId from Applicant a where a.id =:applicantId")
    Optional<Long> findParkingSpaceId(@Param("applicantId") Long applicantId);

    List<Applicant> findByDrawId(Long drawId);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.randomNumber = :randomNumber WHERE a.id = :applicantId")
    void assignRandomNumber(@Param("applicantId") Long applicantId, @Param("randomNumber") double randomNumber);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.parkingSpaceId = :parkingSpaceId WHERE a.id = :winnerId")
    void updateParkingSpaceId(@Param("winnerId") Long winnerId, @Param("parkingSpaceId") Long parkingSpaceId);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.reserveNum = :reserveNum WHERE a.id = :applicantId")
    void updateReserveNum(@Param("applicantId") Long applicantId, @Param("reserveNum") int reserveNum);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.weightedTotalScore = :weight WHERE a.id = :applicantId")
    void updateWeightedTotalScore(@Param("applicantId") Long id, @Param("weight") double weight);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.winningStatus = :winningStatus WHERE a.id = :winnerId")
    void updateWinningStatus(@Param("winnerId") Long winnerId, @Param("winningStatus") WinningStatus winningStatus);

    Page<Applicant> findByDrawId(PageRequest pageRequest, Long drawId);

}
