package com.cruise.parkinglotto.web.dto.drawStatisticsDTO;

import com.cruise.parkinglotto.domain.enums.WeightSection;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinningRatePerWeightSectionDTO {
        private WeightSection weightSection;
        private Integer applicantsCount;
        private Integer winnerCount;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDrawStatisticsResultDTO {
        private Integer applicantsCount;
        private Integer totalSlots;
        private List<ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO> parkingSpaceCompetitionRate;
        private List<WinningRatePerWeightSectionDTO> winningRatePerWeightSection;
    }
}
