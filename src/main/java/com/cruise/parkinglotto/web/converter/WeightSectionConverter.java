package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.DrawStatistics;
import com.cruise.parkinglotto.domain.WeightSectionStatistics;
import com.cruise.parkinglotto.domain.enums.WeightSection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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
}
