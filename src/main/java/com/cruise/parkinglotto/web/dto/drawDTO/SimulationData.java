package com.cruise.parkinglotto.web.dto.drawDTO;

import com.cruise.parkinglotto.domain.enums.WinningStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimulationData {
    private Double randomNumber;
    private Double totalWeightScore;
    private Long parkingSpaceId;
    private Integer reserveNum;
    private WinningStatus winningStatus;
}