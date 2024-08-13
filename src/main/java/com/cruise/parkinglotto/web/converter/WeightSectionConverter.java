package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.WeightSectionStatistics;
import com.cruise.parkinglotto.domain.enums.WeightSection;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;

public class WeightSectionConverter {
    public static WeightSectionStatistics toWeightSectionStatistics(WeightSection section, Draw draw) {
        WeightSectionStatistics statistics = WeightSectionStatistics.builder()
                .section(section)
                .applicantsCount(0)
                .winnerCount(0)
                .draw(draw)
                .build();
        return statistics;
    }

    public static DrawStatisticsResponseDTO.WinningRatePerWeightSectionDTO toWinningRatePerWeightSectionDTO(WeightSectionStatistics weightSectionStatistics) {
        return DrawStatisticsResponseDTO.WinningRatePerWeightSectionDTO.builder()
                .weightSection(weightSectionStatistics.getSection())
                .applicantsCount(weightSectionStatistics.getApplicantsCount())
                .winnerCount(weightSectionStatistics.getWinnerCount())
                .build();
    }
}