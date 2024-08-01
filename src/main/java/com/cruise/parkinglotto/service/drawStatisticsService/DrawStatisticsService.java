package com.cruise.parkinglotto.service.drawStatisticsService;

import com.cruise.parkinglotto.domain.DrawStatistics;

import java.util.List;

public interface DrawStatisticsService {
    List<DrawStatistics> getRecentDrawStatistics();
}
