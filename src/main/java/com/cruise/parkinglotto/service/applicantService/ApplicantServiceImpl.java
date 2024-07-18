package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.MemberRepository;
import com.cruise.parkinglotto.web.dto.ApplicantResponseDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicantServiceImpl implements ApplicantService{

    private final ApplicantRepository applicantRepository;

    public WinnerCancelResponseDTO giveUpMyWinning(Long memberId) {

        Long applicantId = applicantRepository.findByMember(memberId);
        Optional<Applicant> findApplicant = applicantRepository.findById(applicantId);
        WinningStatus winningStatus = findApplicant.get( ).getWinningStatus( );

        if (winningStatus == WinningStatus.WINNER) {
            findApplicant.get( ).cancelWinningStatus( );
        }
        LocalDateTime cancelAt =  LocalDateTime.now();
        String applicantName = findApplicant.get().getMember().getNameKo();
        String employeeNo = findApplicant.get().getMember().getEmployeeNo();
        return new WinnerCancelResponseDTO( cancelAt, applicantName ,employeeNo );

    }


}
