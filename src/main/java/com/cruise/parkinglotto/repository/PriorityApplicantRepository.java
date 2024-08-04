package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriorityApplicantRepository extends JpaRepository<PriorityApplicant, Long> {
    Page<PriorityApplicant> findByDrawIdAndApprovalStatus(PageRequest pageRequest, Long drawId, ApprovalStatus approvalStatus);

    Optional<PriorityApplicant> findById(Long priorityApplicantId);

    Optional<PriorityApplicant> findByDrawIdAndMemberId(Long drawId, Long memberId);
}
