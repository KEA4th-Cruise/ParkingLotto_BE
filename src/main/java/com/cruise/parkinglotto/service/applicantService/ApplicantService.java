package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApplicantService {
    Page<Applicant> getApplicantList(Integer page, Long drawId);

    ApplicantResponseDTO.MyApplyInfoDTO getMyApplyInfo(Long memberId, Long drawId);

    Page<ApplicantResponseDTO.GetMyApplyResultDTO> getApplyResultList(Long memberId, Integer page);

    void drawApply(List<MultipartFile> certificateDocs, ApplicantRequestDTO.GeneralApplyDrawRequestDTO applyDrawRequestDTO, String accountId, Long drawId);

    Page<Applicant> searchApplicant(Integer page, String keyword, Long drawId);

    void cancelApply(String accountId, Long drawId);

    Page<Applicant> searchWinner(Integer page, String keyword, Long drawId);
}
