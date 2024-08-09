package com.cruise.parkinglotto.service.priorityApplicantService;

import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface PriorityApplicantService {

    PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO getPriorityApplicantList(Integer page, Long drawId, ApprovalStatus approvalStatus);

    PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long priorityApplicantId);

    PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO getPriorityApplicantDetails(Long drawId, Long priorityApplicantId);

    PriorityApplicantResponseDTO.RejectPriorityResultDTO rejectPriority(Long drawId, Long priorityApplicantId) throws MessagingException, NoSuchAlgorithmException;

    void drawPriorityApply(List<MultipartFile> GeneralCertificateDocs, List<MultipartFile> priorityCertificateDocs, PriorityApplicantRequestDTO.PriorityApplyDrawRequestDTO priorityApplyDrawRequestDTO, String accountId, Long drawId);

    PriorityApplicantResponseDTO.AssignPriorityResultListDTO assignPriority(Long drawId) throws MessagingException, NoSuchAlgorithmException;

    void cancelPriorityApply(String accountId, Long drawId);

    PriorityApplicantResponseDTO.CancelPriorityAssignResultDTO cancelPriorityAssign(Long drawId, Long priorityApplicantId) throws MessagingException, NoSuchAlgorithmException;

    PriorityApplicantResponseDTO.getMyPriorityApplyInformationDTO getMyPriorityApplyInformation(Long drawId, String accountId);
    
}