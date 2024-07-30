package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;

public interface ApplicantService {
    Page<Applicant> getApplicantList(Integer page, Long drawId);

    ApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long applicantId);

    ApplicantResponseDTO.MyApplyInfoDTO getMyApplyInfo(Long memberId, Long drawId);
}
