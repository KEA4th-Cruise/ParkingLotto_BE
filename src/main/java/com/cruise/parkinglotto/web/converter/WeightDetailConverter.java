package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.WeightDetails;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;

import static com.cruise.parkinglotto.domain.QWeightDetails.weightDetails;

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
                .carCommuteTime(0)
                .recentLossCount(0)
                .trafficCommuteTime(0)
                .distance(0.00)
                .build();
        return weightDetails;
    }

    public static WeightDetails makeWeightDetails(Member member, String address, WorkType workType, Integer trafficCommuteTime, Integer carCommuteTime, Double distance) {
        return WeightDetails.builder()
                .member(member)
                .address(address)
                .workType(workType)
                .trafficCommuteTime(trafficCommuteTime)
                .carCommuteTime(carCommuteTime)
                .distance(distance)
                .recentLossCount(0)
                .build();
    }

}
