package com.cruise.parkinglotto.service.applicantService;

import com.cruise.convert.ApplicantConverter;
import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;

    public WinnerCancelResponseDTO giveUpMyWinning(Long memberId) {

        Long applicantId = applicantRepository.findByMember(memberId);
        Applicant findApplicant = applicantRepository.findById(applicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        WinningStatus winningStatus = findApplicant.getWinningStatus( );

        if (winningStatus == WinningStatus.WINNER) {
            findApplicant.cancelWinningStatus( );
        }

        return ApplicantConverter.toWinnerCancelResponseDTO(findApplicant);

    }


}
