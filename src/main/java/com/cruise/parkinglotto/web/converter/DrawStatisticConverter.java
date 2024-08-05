package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class DrawStatisticConverter {
    public static DrawStatistics toDrawStatistics(Draw draw, List<Applicant> applicants, int totalSlots) {
        DrawStatistics drawStatistics =
                DrawStatistics.builder()
                        .competitionRate((double) applicants.size() / totalSlots)
                        .totalApplicants(applicants.size())
                        .applicantsWeightAvg(applicants.stream().mapToDouble(Applicant::getWeightedTotalScore).average().orElse(0))
                        .draw(draw)
                        .build();
        return drawStatistics;
    }
}
