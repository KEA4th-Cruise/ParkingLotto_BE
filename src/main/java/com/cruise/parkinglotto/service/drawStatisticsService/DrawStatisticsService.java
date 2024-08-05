package com.cruise.parkinglotto.service.drawStatisticsService;

import com.cruise.parkinglotto.domain.DrawStatistics;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;

import java.util.List;

public interface DrawStatisticsService {
    List<DrawStatistics> getRecentDrawStatistics();

    void updateDrawStatistics(Long drawId);

    DrawStatisticsResponseDTO.GetDrawStatisticsResultDTO getDrawStatistics(Long drawId);

}
