package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
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

    @Override
    public void assignRandomNumber(List<Applicant> applicants, String seed) {
        //해당 회차 시드로 생성된 첫 난수를 맨 처음 멤버에게 부여
        Random rand = new Random(seed.hashCode());
        double randomNumber = rand.nextDouble();
        applicantRepository.assignRandomNumber(applicants.get(0).getId(), randomNumber);
        //해당 난수를 시드로 하여 모든 신청자들에게 난수 부여
        for (int i = 1; i < applicants.size(); i++) {
            rand = new Random(Double.doubleToLongBits(randomNumber));
            randomNumber = rand.nextDouble();
            applicantRepository.assignRandomNumber(applicants.get(i).getId(), randomNumber);
        }
    }
}
