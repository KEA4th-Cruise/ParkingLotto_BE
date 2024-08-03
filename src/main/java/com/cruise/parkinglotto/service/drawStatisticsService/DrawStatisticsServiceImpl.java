package com.cruise.parkinglotto.service.drawStatisticsService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.DrawStatistics;
import com.cruise.parkinglotto.domain.WeightSectionStatistics;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WeightSection;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.DrawStatisticsRepository;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DrawStatisticsServiceImpl implements DrawStatisticsService {

    private final DrawRepository drawRepository;
    private final ApplicantRepository applicantRepository;
    private final DrawStatisticsRepository drawStatisticsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DrawStatistics> getRecentDrawStatistics() {
        List<Draw> drawList = drawRepository.findTop5ByTypeOrderByUsageStartAtDesc(DrawType.GENERAL);
        List<DrawStatistics> drawStatisticsList = drawList.stream()
                .map(Draw::getDrawStatistics)
                .toList();
        return drawStatisticsList;
    }

    @Override
    @Transactional
    public void updateDrawStatistics(Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow();
        int totalSlots = draw.getTotalSlots();
        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
        DrawStatistics drawStatistics =
                DrawStatistics.builder()
                        .competitionRate((double) applicants.size() / totalSlots)
                        .totalApplicants(applicants.size())
                        .applicantsWeightAvg(applicants.stream().mapToDouble(Applicant::getWeightedTotalScore).average().orElse(0))
                        .draw(draw)
                        .build();
        drawStatisticsRepository.save(drawStatistics);
    }
}
