package com.cruise.parkinglotto.web.dto.drawDTO;

import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
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
        private String description;
        private Integer totalSlots;
        private List<GetNameAndUrlParkingSpaceResultDTO> getNameAndUrlParkingSpaceResultDTO;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawMemberResultResponseDTO {
        private List<ApplicantResponseDTO.ApplicantResultDTO> applicants;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
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
    public static class ConfirmDrawCreationResultDTO {
        private Long drawId;
        private String title;
        LocalDateTime usageStartAt;
        LocalDateTime usageEndAt;
        ParkingSpaceResponseDTO.ParkingSpacePreviewListDTO parkingSpacePreviewListDTO;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulateDrawResponseDTO {
        private Long drawId;
        private String seed;
        private List<SimulateApplicantDTO> winners;
        private int totalApplicants; // 전체 응모자 수
    }

    @Builder
    @Getter
    public static class SimulateApplicantDTO {
        private Long id;
        private String name;
        private double weightedTotalScore;
        private double randomNumber;
        private Long parkingSpaceId;
        private int reserveNum;
        private WinningStatus winningStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDrawOverviewResultDTO {   //  메인페이지에 추첨 정보를 띄우기 위한 DTO
        private Long drawId;
        private String title;
        private DrawStatus status;
        private boolean isApplied;
        private LocalDateTime drawStartAt;
        private LocalDateTime drawEndAt;
        private LocalDateTime usageStartAt;
        private LocalDateTime usageEndAt;
        private Integer applicantsCount;
        private Integer totalSlots;
        private List<ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO> parkingSpaceCompetitionRateDTOList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawResultExcelDTO {
        private String URL;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDrawInfoDetailDTO {   //  시뮬레이션 시작할때 사용되는 추첨 회차 정보를 띄우기 위한 DTO
        private Long drawId;
        private String title;
        private Integer applicantsCount;
        private Integer totalSlots;
        private String seed;
        private List<DrawResponseDTO.SeedDetailDTO> seedDetail;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeedDetailDTO {   //  추첨 회차의 시드 정보를 띄우기 위한 DTO
        private String accountId;
        private String userSeed;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawPreviewDTO {
        private Long drawId;
        private DrawType drawType;
        private String drawTitle;
        private DrawStatus drawStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDrawListResultDTO {
        private List<String> yearList;
        private List<DrawPreviewDTO> drawList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetYearsFromDrawListDTO {
        private List<String> yearList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RealTimeDrawInfo {
        private Integer applicantsCount;
        private Integer totalSlots;
        private List<ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO> parkingSpaceCompetitionRateDTOList;
    }
}