package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final DrawRepository drawRepository;
    private final ApplicantRepository applicantRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Applicant> getApplicantList(Integer page, Long drawId) {
        drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Page<Applicant> applicantList = applicantRepository.findByDrawId(PageRequest.of(page, 5), drawId);
        return applicantList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicantResponseDTO.GetMyApplyResultDTO> getApplyResultList(Long memberId) {

        List<Applicant> findApplicants = applicantRepository.findApplicantListByMemberId(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        List<ApplicantResponseDTO.GetMyApplyResultDTO> result = findApplicants.stream()
                .map(a -> ApplicantConverter.toGetMyApplyResultDTO(a))
                .collect(Collectors.toList());

        return result;

    }
}
