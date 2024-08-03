package com.cruise.parkinglotto.service.priorityApplicantService;

import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import org.springframework.data.domain.Page;

public interface PriorityApplicantService {

    Page<PriorityApplicant> getPriorityApplicantList(Integer page, Long drawId, ApprovalStatus approvalStatus);

    PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long priorityApplicantId);

    PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO getPriorityApplicantDetails(Long drawId, Long priorityApplicantId);

    PriorityApplicantResponseDTO.RejectPriorityResultDTO rejectPriority(Long drawId, Long priorityApplicantId);
}