package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriorityApplicantRepository extends JpaRepository<PriorityApplicant, Long> {
    Page<PriorityApplicant> findPriorityApplicantPageByDrawIdAndApprovalStatus(PageRequest pageRequest, Long drawId, ApprovalStatus approvalStatus);

    Optional<PriorityApplicant> findPriorityApplicantById(Long priorityApplicantId);

}
