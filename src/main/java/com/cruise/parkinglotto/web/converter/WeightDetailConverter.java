package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;

public class WeightDetailConverter {
    public static WeightDetailResponseDTO.GetMemberWeightDTO toGetMemberWeightDTO(WorkType workType, int carCommuteType, int trafficCommuteTime, double distance, int recentLossCount, String address, int difference) {
        return WeightDetailResponseDTO.GetMemberWeightDTO.builder().
                workType(workType).
                carCommuteTime(carCommuteType).
                trafficCommuteTime(trafficCommuteTime).
                distance(distance).
                recentLossCount(recentLossCount).
                address(address).
                difference(difference).
                build();
    }
}
