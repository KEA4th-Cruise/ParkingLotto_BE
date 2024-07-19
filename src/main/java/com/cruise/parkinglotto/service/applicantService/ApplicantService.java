package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO.*;
import org.springframework.stereotype.Service;

@Service
public interface ApplicantService {

    public WinnerCancelResponseDTO giveUpMyWinning(Long memberId);

}
