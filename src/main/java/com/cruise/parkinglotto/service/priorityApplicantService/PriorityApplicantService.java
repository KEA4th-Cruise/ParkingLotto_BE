package com.cruise.parkinglotto.service.priorityApplicantService;

import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PriorityApplicantService {

    PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO getPriorityApplicantList(Integer page, Long drawId, ApprovalStatus approvalStatus);

    PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long priorityApplicantId);

    PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO getPriorityApplicantDetails(Long drawId, Long priorityApplicantId);

    PriorityApplicantResponseDTO.RejectPriorityResultDTO rejectPriority(Long drawId, Long priorityApplicantId);

    void drawPriorityApply(List<MultipartFile> GeneralCertificateDocs, List<MultipartFile> priorityCertificateDocs, PriorityApplicantRequestDTO.PriorityApplyDrawRequestDTO priorityApplyDrawRequestDTO, String accountId, Long drawId);

    PriorityApplicantResponseDTO.AssignPriorityResultListDTO assignPriority(Long drawId);

    void cancelPriorityApply(String accountId, Long drawId);
}