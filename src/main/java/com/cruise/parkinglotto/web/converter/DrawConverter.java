package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.RequiredArgsConstructor;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class DrawConverter {
    public static DrawResponseDTO.GetCurrentDrawInfoDTO toGetCurrentDrawInfo(Draw draw, List<ParkingSpace> parkingSpace) {

        List<ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO> dto=ParkingSpaceConverter.toGetNameAndUrlParkingResponse(parkingSpace);

        return DrawResponseDTO.GetCurrentDrawInfoDTO.builder()
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .mapImageUrl(draw.getMapImageUrl())
                .getNameAndUrlParkingSpaceResultDTO(dto)
                .build();
        }

    public static DrawResponseDTO.DrawResultResponseDTO toDrawResultResponseDTO(Draw draw, List<Applicant> applicants, Map<Long, String> parkingSpaceNames) {
        List<ApplicantResponseDTO.ApplicantResultDTO> applicantInfoDTOList = applicants.stream()
                .map(applicant -> ApplicantResponseDTO.ApplicantResultDTO.builder()
                        .weightedTotalScore(applicant.getWeightedTotalScore())
                        .winningStatus(applicant.getWinningStatus())
                        .parkingSpaceName(parkingSpaceNames.get(applicant.getParkingSpaceId()))
                        .reserveNum(applicant.getReserveNum())
                        .userSeedIndex(applicant.getUserSeedIndex())
                        .userSeed(applicant.getUserSeed())
                        .firstChoice(parkingSpaceNames.get(applicant.getFirstChoice()))
                        .secondChoice(parkingSpaceNames.get(applicant.getSecondChoice()))
                        .userName(applicant.getMember().getNameKo())
                        .build())
                .toList();

        return DrawResponseDTO.DrawResultResponseDTO.builder().
                drawId(draw.getId())
                .drawType(draw.getType().name())
                .title(draw.getTitle())
                .seedNum(draw.getSeedNum())
                .totalSlots(draw.getTotalSlots())
                .year(draw.getYear())
                .quarter(draw.getQuarter())
                .applicants(applicantInfoDTOList).
                build();
    }

}
