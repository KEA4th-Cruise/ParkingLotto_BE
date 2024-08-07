package com.cruise.parkinglotto.repository.querydsl;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ApplicantCustomRepository {

    Page<Applicant> findApplicantByDrawIdAndKeyword(PageRequest pageRequest, String keyword, Long drawId);

    Page<Applicant> findWinnerByDrawIdAndKeyword(PageRequest pageRequest, String keyword, Long drawId);
}
