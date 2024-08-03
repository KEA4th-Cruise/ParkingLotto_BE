package com.cruise.parkinglotto.service.weightSectionStatisticsService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.WeightSectionStatistics;
import com.cruise.parkinglotto.domain.enums.WeightSection;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.WeightSectionStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeightSectionStatisticsServiceImpl implements WeightSectionStatisticsService {
    private final ApplicantRepository applicantRepository;
    private final WeightSectionStatisticsRepository weightSectionStatisticsRepository;
    private final DrawRepository drawRepository;

    @Override
    @Transactional
    public void updateWeightSectionStatistics(Long drawId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            log.error("No active transaction. updateWeightSectionStatistics must be called within a transaction.");
            throw new IllegalStateException("No active transaction");
        }
        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
        Map<WeightSection, WeightSectionStatistics> statisticsMap = initializeStatisticsMap(drawId);

        for (Applicant applicant : applicants) {
            WeightSection section = determineSection(applicant.getWeightedTotalScore());
            WeightSectionStatistics statistics = statisticsMap.get(section);
            statistics.updateApplicantsCount();
            log.info("applicant.getWinningStatus() = " + applicant.getWinningStatus());
            if ((applicant.getWinningStatus()).equals(WinningStatus.WINNER)) {
                System.out.println((applicant.getWinningStatus()) == WinningStatus.WINNER);
                statistics.updateWinnerCount();
            }
        }
        weightSectionStatisticsRepository.saveAll(statisticsMap.values());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Map<WeightSection, WeightSectionStatistics> initializeStatisticsMap(Long drawId) {
        Map<WeightSection, WeightSectionStatistics> map = new EnumMap<>(WeightSection.class);
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        for (WeightSection section : WeightSection.values()) {
            WeightSectionStatistics statistics = WeightSectionStatistics.builder()
                    .section(section)
                    .applicantsCount(0)
                    .winnerCount(0)
                    .draw(draw)
                    .build();
            WeightSectionStatistics ws = weightSectionStatisticsRepository.saveAndFlush(statistics);
            log.info("Created WeightSectionStatistics: {}", ws.getId());
            map.put(section, statistics);
        }
        return map;
    }

    private WeightSection determineSection(double score) {
        if (score <= 20) {
            return WeightSection.SECTION1;
        } else if (score <= 40) {
            return WeightSection.SECTION2;
        } else if (score <= 60) {
            return WeightSection.SECTION3;
        } else if (score <= 80) {
            return WeightSection.SECTION4;
        } else {
            return WeightSection.SECTION5;
        }
    }
}
