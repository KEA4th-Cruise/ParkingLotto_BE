package com.cruise.parkinglotto.service.applicantService;


import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO.*;
import org.springframework.stereotype.Service;
import com.cruise.parkinglotto.domain.Applicant;

import org.springframework.data.domain.Page;

import java.util.List;

@Service
public interface ApplicantService {

    public WinnerCancelResponseDTO giveUpMyWinning(Long memberId);
    Page<Applicant> getApplicantList(Integer page, Long drawId);

}
