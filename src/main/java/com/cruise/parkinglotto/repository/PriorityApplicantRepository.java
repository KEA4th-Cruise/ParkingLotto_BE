package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PriorityApplicantRepository extends JpaRepository<PriorityApplicant, Long> {
    Page<PriorityApplicant> findByDrawIdAndApprovalStatus(PageRequest pageRequest, Long drawId, ApprovalStatus approvalStatus);

    Optional<PriorityApplicant> findById(Long priorityApplicantId);

    Optional<PriorityApplicant> findByDrawIdAndMemberId(Long drawId, Long memberId);

    @Query("SELECT pa FROM PriorityApplicant pa WHERE pa.draw.id = :drawId AND pa.approvalStatus = :approvalStatus")
    List<PriorityApplicant> findApprovedApplicantsByDrawId(@Param("drawId") Long drawId, @Param("approvalStatus") ApprovalStatus approvalStatus);

    @Transactional
    void deleteByDrawIdAndMember(Long drawId, Member member);

    @Query("SELECT pa FROM PriorityApplicant pa WHERE pa.member.id = :memberId")
    List<PriorityApplicant> findByMemberId(@Param("memberId") Long memberId);
}
