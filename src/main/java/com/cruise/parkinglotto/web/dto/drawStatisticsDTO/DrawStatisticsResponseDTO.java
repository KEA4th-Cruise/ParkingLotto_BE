package com.cruise.parkinglotto.web.dto.drawStatisticsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class DrawStatisticsResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawCompetitionRateDTO {
        private Long drawId;
        private String drawTitle;
        private Double competitionRate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawCompetitionRateListDTO {
        private List<DrawCompetitionRateDTO> drawCompetitionRateListDTO;
    }
}
