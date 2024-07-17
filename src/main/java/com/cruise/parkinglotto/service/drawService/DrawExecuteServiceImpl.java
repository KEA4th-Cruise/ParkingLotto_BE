package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrawExecuteServiceImpl implements DrawExecuteService {

    private final ApplicantRepository applicantRepository;
    private final DrawRepository drawRepository;

    @Override
    public void updateSeedNum(Long drawId) {
        try {
            List<Applicant> applicants = applicantRepository.findByDrawId(drawId);

            if (applicants == null || applicants.isEmpty()) {
                throw new IllegalArgumentException("해당 회차에 신청자가 없습니다. 해당 회차 ID : " + drawId);
            }
            String seed = applicants.stream()
                    .map(Applicant::getUserSeed)
                    .collect(Collectors.joining());
            drawRepository.updateSeedNum(drawId, seed);
        } catch (IllegalArgumentException e) {
            System.err.println("Error : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred while retrieving applicants for draw ID: " + drawId);
        }
    }
}
