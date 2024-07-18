package com.cruise.parkinglotto.service;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final MemberRepository memberRepository;


    public void giveUpMyWinning(Long memberId) {

        Long applicantId = applicantRepository.findByMember(memberId);
        Optional<Applicant> findApplicant = applicantRepository.findById(applicantId);
        WinningStatus winningStatus = findApplicant.get( ).getWinningStatus( );

        if(winningStatus==WinningStatus.WINNER) {
            findApplicant.get().cancelWinningStatus();
        }

    }



}
