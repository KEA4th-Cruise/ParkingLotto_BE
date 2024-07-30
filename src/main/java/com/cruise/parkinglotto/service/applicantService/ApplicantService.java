package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ApplicantService {
    Page<Applicant> getApplicantList(Integer page, Long drawId);

    List<ApplicantResponseDTO.GetMyApplyResultDTO> getApplyResultList(Long memberId);
}
