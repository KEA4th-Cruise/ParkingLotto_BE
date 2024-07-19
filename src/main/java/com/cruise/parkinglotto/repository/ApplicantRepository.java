package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    List<Applicant> findByDrawId(Long drawId);

    @Modifying
    @Query("UPDATE Applicant a SET a.randomNumber = :randomNumber WHERE a.id = :applicantId")
    void assignRandomNumber(@Param("applicantId") Long id, @Param("randomNumber") double randomNumber);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.parkingSpaceId = :parkingSpaceId WHERE a.id = :id")
    void updateParkingSpaceId(@Param("id") Long id, @Param("parkingSpaceId") Long parkingSpaceId);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.reserveNum = :reserveNum WHERE a.id = :id")
    void updateReserveNum(@Param("id") Long id, @Param("reserveNum") int reserveNum);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.weightedTotalScore = :weight WHERE a.id = :applicantId")
    void updateWeightedTotalScore(@Param("applicantId") Long id, @Param("weight") double weight);

    @Modifying
    @Transactional
    @Query("UPDATE Applicant a SET a.winningStatus = :winningStatus WHERE a.id = :id")
    void updateWinningStatus(@Param("id") Long id, @Param("winningStatus") WinningStatus winningStatus);
}
