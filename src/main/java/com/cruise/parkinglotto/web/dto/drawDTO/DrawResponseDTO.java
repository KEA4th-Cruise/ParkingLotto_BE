package com.cruise.parkinglotto.web.dto.drawDTO;

import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO;

import java.time.LocalDateTime;
import java.util.List;

public class DrawResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCurrentDrawInfoDTO {
        private LocalDateTime drawStartAt;
        private LocalDateTime drawEndAt;
        private LocalDateTime usageStartAt;
        private LocalDateTime usageEndAt;
        private String mapImageUrl;
        private List<GetNameAndUrlParkingSpaceResultDTO> getNameAndUrlParkingSpaceResultDTO;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawResultResponseDTO {
        private Long drawId;
        private String drawType;
        private String title;
        private String seedNum;
        private Integer totalSlots;
        private String year;
        private String quarter;
        private List<ApplicantResponseDTO.ApplicantResultDTO> applicants;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDrawResultDTO {
        private Long drawId;
        private String title;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfirmDrawCreationResultDTO{
        private Long drawId;
        private String title;
        LocalDateTime usageStartAt;
        LocalDateTime usageEndAt;
        ParkingSpaceResponseDTO.ParkingSpacePreviewListDTO parkingSpacePreviewListDTO;
    }
}