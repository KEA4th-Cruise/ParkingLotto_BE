package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    List<Applicant> findByDrawId(Long drawId);
}
