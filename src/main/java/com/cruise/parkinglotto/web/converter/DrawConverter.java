package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.RequiredArgsConstructor;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.SimulationData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class DrawConverter {
    public static DrawResponseDTO.GetCurrentDrawInfoDTO toGetCurrentDrawInfo(Draw draw, List<ParkingSpace> parkingSpace) {

        List<ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO> dto = ParkingSpaceConverter.toGetNameAndUrlParkingResponse(parkingSpace);

        return DrawResponseDTO.GetCurrentDrawInfoDTO.builder()
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .mapImageUrl(draw.getMapImageUrl())
                .description(draw.getDescription())
                .totalSlots(draw.getTotalSlots())
                .getNameAndUrlParkingSpaceResultDTO(dto)
                .build();
    }

    public static DrawResponseDTO.DrawMemberResultResponseDTO toDrawResultResponseDTO (Page<Applicant> applicantsPage) {
        List<ApplicantResponseDTO.ApplicantResultDTO> applicantInfoDTOList = applicantsPage.stream()
                .map(applicant -> ApplicantResponseDTO.ApplicantResultDTO.builder()
                        .weightedTotalScore(applicant.getWeightedTotalScore())
                        .winningStatus(applicant.getWinningStatus())
                        .reserveNum(applicant.getReserveNum())
                        .userSeedIndex(applicant.getUserSeedIndex())
                        .userSeed(applicant.getUserSeed())
                        .userName(maskName(applicant.getMember().getNameKo()))
                        .build())
                .collect(Collectors.toList());

        return DrawResponseDTO.DrawMemberResultResponseDTO.builder()
                .applicants(applicantInfoDTOList)
                .listSize(applicantsPage.getSize())
                .totalPage(applicantsPage.getTotalPages())
                .totalElements(applicantsPage.getTotalElements())
                .isFirst(applicantsPage.isFirst())
                .isLast(applicantsPage.isLast())
                .build();
    }

    public static Draw toDraw(DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO, String title, String mapImageUrl, String year, String quarter) {
        return Draw.builder()
                .title(title)
                .type(createDrawRequestDTO.getType())
                .drawStartAt(createDrawRequestDTO.getDrawStartAt())
                .drawEndAt(createDrawRequestDTO.getDrawEndAt())
                .usageStartAt(createDrawRequestDTO.getUsageStartAt())
                .usageEndAt(createDrawRequestDTO.getUsageEndAt())
                .mapImageUrl(mapImageUrl)
                .description(createDrawRequestDTO.getDescription())
                .year(year)
                .quarter(quarter)
                .status(DrawStatus.PENDING)
                .totalSlots(0)
                .confirmed(false)
                .build();
    }

    public static DrawResponseDTO.CreateDrawResultDTO toCreateDrawResultDTO(Draw draw) {
        return DrawResponseDTO.CreateDrawResultDTO.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .createdAt(draw.getCreatedAt())
                .build();
    }

    public static DrawResponseDTO.ConfirmDrawCreationResultDTO toConfirmDrawCreationResultDTO(Draw draw, List<ParkingSpace> parkingSpaceList) {
        return DrawResponseDTO.ConfirmDrawCreationResultDTO.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .parkingSpacePreviewListDTO(ParkingSpaceConverter.toParkingSpacePreviewListDTO(parkingSpaceList))
                .build();
    }

    public static DrawResponseDTO.SimulateDrawResponseDTO toSimulateDrawResponseDTO(Long drawId, String seed, Map<Long, SimulationData> simulationDataMap, List<DrawResponseDTO.SimulateApplicantDTO> pagedApplicants, int totalApplicants) {
        return DrawResponseDTO.SimulateDrawResponseDTO.builder()
                .drawId(drawId)
                .seed(seed)
                .totalApplicants(totalApplicants)
                .winners(pagedApplicants)
                .totalApplicants(totalApplicants)
                .build();
    }

    public static DrawResponseDTO.SimulateApplicantDTO toSimulateApplicantDTO(Applicant applicant, SimulationData simData) {
        return DrawResponseDTO.SimulateApplicantDTO.builder()
                .id(applicant.getId())
                .name(maskName(applicant.getMember().getNameKo()))
                .weightedTotalScore(Double.parseDouble(String.format("%.1f", simData.getTotalWeightScore())))
                .randomNumber(simData.getRandomNumber())
                .parkingSpaceId(simData.getParkingSpaceId())
                .reserveNum(simData.getReserveNum())
                .winningStatus(simData.getWinningStatus())
                .build();
    }

    private static String maskName(String name) {
        if (name == null || name.length() < 2) {
            return name; // 이름이 너무 짧으면 마스킹하지 않음
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*"; // 이름이 2글자인 경우, 맨 뒷글자만 가림
        }
        StringBuilder maskedName = new StringBuilder(name.length());
        maskedName.append(name.charAt(0)); // 첫 글자 추가
        for (int i = 1; i < name.length() - 1; i++) {
            maskedName.append('*'); // 중간 글자는 *로 대체
        }
        maskedName.append(name.charAt(name.length() - 1)); // 마지막 글자 추가
        return maskedName.toString();
    }

    public static DrawResponseDTO.GetDrawOverviewResultDTO toGetDrawOverviewResultDTO(Boolean isApplied, Integer applicantsCount, Draw draw, List<ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO> parkingSpaceCompetitionRateDTOList) {
        return DrawResponseDTO.GetDrawOverviewResultDTO.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .status(draw.getStatus())
                .isApplied(isApplied)
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .applicantsCount(applicantsCount)
                .totalSlots(draw.getTotalSlots())
                .parkingSpaceCompetitionRateDTOList(parkingSpaceCompetitionRateDTOList)
                .build();
    }

    public static DrawResponseDTO.DrawResultExcelDTO toDrawResultExcelDTO(String url) {
        return DrawResponseDTO.DrawResultExcelDTO.builder()
                .URL(url)
                .build();
    }

    public static DrawResponseDTO.GetDrawInfoDetailDTO toGetDrawInfoDetail(Draw draw, List<Applicant> applicants) {
        return DrawResponseDTO.GetDrawInfoDetailDTO.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .applicantsCount(applicants.size())
                .totalSlots(draw.getTotalSlots())
                .seed(draw.getSeedNum())
                .seedDetail(applicants.stream()
                        .map(applicant -> DrawResponseDTO.SeedDetailDTO.builder()
                                .accountId(applicant.getMember().getAccountId())
                                .userSeed(applicant.getUserSeed())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static DrawResponseDTO.DrawPreviewDTO toDrawPreviewDTO(Draw draw) {
        return DrawResponseDTO.DrawPreviewDTO.builder()
                .drawId(draw.getId())
                .drawType(draw.getType())
                .drawStatus(draw.getStatus())
                .drawTitle(draw.getTitle())
                .build();
    }

    public static DrawResponseDTO.GetDrawListResultDTO toGetDrawListResultDTO(List<String> yearList, List<Draw> drawList) {
        return DrawResponseDTO.GetDrawListResultDTO.builder()
                .yearList(yearList)
                .drawList(drawList.stream()
                        .map(DrawConverter::toDrawPreviewDTO)
                        .toList())
                .build();
    }

    public static DrawResponseDTO.GetYearsFromDrawListDTO toGetYearsFromDrawListDTO(List<String> yearList) {
        return DrawResponseDTO.GetYearsFromDrawListDTO.builder()
                .yearList(yearList)
                .build();
    }

}
