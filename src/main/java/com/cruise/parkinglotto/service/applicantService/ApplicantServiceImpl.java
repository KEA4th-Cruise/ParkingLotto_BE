package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
        if (applicantList == null || applicantList.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }
        return applicantList;
    }
}
