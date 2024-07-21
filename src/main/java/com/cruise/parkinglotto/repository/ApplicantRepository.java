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
    void assignRandomNumber(@Param("applicantId") Long applicantId, @Param("randomNumber") double randomNumber);

    @Modifying
    @Query("UPDATE Applicant a SET a.parkingSpaceId = :parkingSpaceId WHERE a.id = :winnerId")
    void updateParkingSpaceId(@Param("winnerId") Long winnerId, @Param("parkingSpaceId") Long parkingSpaceId);

    @Modifying
    @Query("UPDATE Applicant a SET a.reserveNum = :reserveNum WHERE a.id = :applicantId")
    void updateReserveNum(@Param("applicantId") Long applicantId, @Param("reserveNum") int reserveNum);

    @Modifying
    @Query("UPDATE Applicant a SET a.weightedTotalScore = :weight WHERE a.id = :applicantId")
    void updateWeightedTotalScore(@Param("applicantId") Long id, @Param("weight") double weight);

    @Modifying
    @Query("UPDATE Applicant a SET a.winningStatus = :winningStatus WHERE a.id = :winnerId")
    void updateWinningStatus(@Param("winnerId") Long winnerId, @Param("winningStatus") WinningStatus winningStatus);
}
