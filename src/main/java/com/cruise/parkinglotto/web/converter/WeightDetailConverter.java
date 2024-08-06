package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.WeightDetails;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;

public class WeightDetailConverter {
    public static WeightDetailResponseDTO.GetMemberWeightDTO toGetMemberWeightDTO(WorkType workType, int carCommuteType, int trafficCommuteTime, double distance, int recentLossCount, String address, int difference) {
        return WeightDetailResponseDTO.GetMemberWeightDTO.builder()
                .workType(workType)
                .carCommuteTime(carCommuteType)
                .trafficCommuteTime(trafficCommuteTime)
                .distance(distance)
                .recentLossCount(recentLossCount)
                .address(address)
                .difference(difference)
                .build();
    }

    public static WeightDetailResponseDTO.CalculateWeightResponseDTO toCalculateWeightResponseDTO(double result) {
        return WeightDetailResponseDTO.CalculateWeightResponseDTO.builder()
                .calculateResult(result)
                .build();
    }

    public static WeightDetails toWeightDetails(MemberRequestDTO.MyInfoRequestDTO myInfoRequestDTO, Member findMember) {
        WeightDetails weightDetails = WeightDetails.builder()
                .member(findMember)
                .address(myInfoRequestDTO.getAddress())
                .workType(myInfoRequestDTO.getWorkType())
                .build();
        return weightDetails;
    }

}
