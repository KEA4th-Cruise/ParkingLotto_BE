package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
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
}
