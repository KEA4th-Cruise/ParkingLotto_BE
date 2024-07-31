package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.service.drawService.DrawServiceImpl;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.RequiredArgsConstructor;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.SimulationData;

import java.text.DecimalFormat;
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
                .getNameAndUrlParkingSpaceResultDTO(dto)
                .build();
    }

    public static DrawResponseDTO.DrawResultResponseDTO toDrawResultResponseDTO(Draw draw, List<Applicant> applicants, Map<Long, String> parkingSpaceNames, int totalApplicants) {
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
                .collect(Collectors.toList());

        return DrawResponseDTO.DrawResultResponseDTO.builder()
                .drawId(draw.getId())
                .drawType(draw.getType().name())
                .title(draw.getTitle())
                .seedNum(draw.getSeedNum())
                .totalSlots(draw.getTotalSlots())
                .year(draw.getYear())
                .quarter(draw.getQuarter())
                .totalApplicants(totalApplicants) // 전체 응모자 수 추가
                .applicants(applicantInfoDTOList)
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
}