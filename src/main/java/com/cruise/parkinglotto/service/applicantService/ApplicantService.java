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

    /**
     * 검색 키워드로 신청자 목록에서 신청자를 검색하는 메서드
     * 검색 키워드는 employeeNo 또는 accountId를 받을 수 있고 필터링을 사용하지 않는다.
     *
     * @param searchKeyword
     * @return
     */
    ApplicantResponseDTO.GetApplicantResultDTO searchApplicantBySearchKeyword(String searchKeyword);

    /**
     * 검색 키워드로 당첨자 목록에서 신청자를 검색하는 메서드
     * 검색 키워드는 employeeNo 또는 accountId를 받을 수 있고 필터링을 사용하지 않는다.
     *
     * @param searchKeyword
     * @return
     */
    ApplicantResponseDTO.GetApplicantResultDTO searchWinnerBySearchKeyword(String searchKeyword);

    void cancelApply(String accountId, Long drawId);

}
