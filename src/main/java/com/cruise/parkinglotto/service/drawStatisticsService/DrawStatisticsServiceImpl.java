package com.cruise.parkinglotto.service.drawStatisticsService;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.DrawStatistics;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.repository.DrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrawStatisticsServiceImpl implements DrawStatisticsService {

    private final DrawRepository drawRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DrawStatistics> getRecentDrawStatistics() {
        List<Draw> drawList = drawRepository.findTop5ByTypeOrderByUsageStartAtDesc(DrawType.GENERAL);
        List<DrawStatistics> drawStatisticsList = drawList.stream()
                .map(Draw::getDrawStatistics)
                .toList();
        return drawStatisticsList;
    }
}
