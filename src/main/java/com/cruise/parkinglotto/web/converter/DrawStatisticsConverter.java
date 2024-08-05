package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.DrawStatistics;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.WeightSectionStatistics;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;

import java.util.List;
import java.util.Objects;

public class DrawStatisticsConverter {

    public static DrawStatisticsResponseDTO.DrawCompetitionRateDTO toDrawCompetitionRateDTO(DrawStatistics drawStatistics) {
        return DrawStatisticsResponseDTO.DrawCompetitionRateDTO.builder()
                .competitionRate(drawStatistics.getCompetitionRate())
                .drawId(drawStatistics.getDraw().getId())
                .drawTitle(drawStatistics.getDraw().getTitle())
                .build();
    }

    public static DrawStatisticsResponseDTO.DrawCompetitionRateListDTO toDrawCompetitionRateListDTO(List<DrawStatistics> drawStatisticsList) {
        List<DrawStatisticsResponseDTO.DrawCompetitionRateDTO> drawCompetitionRateDTOList = drawStatisticsList.stream()
                .filter(Objects::nonNull)
                .map(DrawStatisticsConverter::toDrawCompetitionRateDTO)
                .toList();

        DrawStatisticsResponseDTO.DrawCompetitionRateListDTO drawCompetitionRateListDTO = DrawStatisticsResponseDTO.DrawCompetitionRateListDTO.builder()
                .drawCompetitionRateListDTO(drawCompetitionRateDTOList)
                .build();
        return drawCompetitionRateListDTO;
    }

    public static DrawStatisticsResponseDTO.GetDrawStatisticsResultDTO toGetDrawStatisticsResultDTO(Integer applicantCount, Integer totalSlots, List<ParkingSpace> parkingSpaceList, List<WeightSectionStatistics> weightSectionStatisticsList) {
        return DrawStatisticsResponseDTO.GetDrawStatisticsResultDTO.builder()
                .applicantsCount(applicantCount)
                .totalSlots(totalSlots)
                .winningRatePerWeightSection(weightSectionStatisticsList.stream()
                        .map(WeightSectionConverter::toWinningRatePerWeightSectionDTO)
                        .toList())
                .parkingSpaceCompetitionRate(parkingSpaceList.stream()
                        .map(parkingSpace -> ParkingSpaceConverter.toParkingSpaceCompetitionRateDTO(parkingSpace, parkingSpace.getApplicantCount()))
                        .toList())
                .build();
    }
}
