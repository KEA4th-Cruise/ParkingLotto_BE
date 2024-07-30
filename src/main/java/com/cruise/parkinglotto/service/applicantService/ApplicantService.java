package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ApplicantService {
    Page<Applicant> getApplicantList(Integer page, Long drawId);
  
    ApplicantResponseDTO.MyApplyInfoDTO getMyApplyInfo(Long memberId, Long drawId);

    List<ApplicantResponseDTO.GetMyApplyResultDTO> getApplyResultList(Long memberId);
    
    void drawApply(List<MultipartFile> certificateDocs, ApplicantRequestDTO.GeneralApplyDrawRequestDTO applyDrawRequestDTO, String accountId);

}
