package com.cruise.parkinglotto.web.dto.drawDTO;

import com.cruise.parkinglotto.domain.Draw;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO.GetNameAndIdWhichFromIsParkingResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class DrawResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCurrentDrawInfoDTO{
        private LocalDateTime drawStartAt;
        private LocalDateTime drawEndAt;
        private LocalDateTime usageStartAt;
        private LocalDateTime usageEndAt;
        private String mapImageUrl;
        private List<GetNameAndIdWhichFromIsParkingResponseDTO> getNameAndIdWhichFromIsParkingResponseDTO;
    }
}
