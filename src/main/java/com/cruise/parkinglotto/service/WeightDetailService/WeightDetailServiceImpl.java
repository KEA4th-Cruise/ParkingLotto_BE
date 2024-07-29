package com.cruise.parkinglotto.service.WeightDetailService;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.WeightDetails;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.WeightDetailsRepository;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cruise.parkinglotto.web.converter.WeightDetailConverter.toGetMemberWeightDTO;

@Service
@RequiredArgsConstructor
public class WeightDetailServiceImpl implements WeightDetailService{
    private final JwtUtils jwtUtils;
    private final MemberService memberService;
    private final WeightDetailsRepository weightDetailsRepository;

    @Override
    public WeightDetailResponseDTO.GetMemberWeightDTO getMemberWeight(HttpServletRequest httpServletRequest) {
        String accountId = jwtUtils.getAccountIdFromRequest(httpServletRequest);

        Member member = memberService.getMemberByAccountId(accountId);

        WeightDetails weightDetail = weightDetailsRepository.findByMemberId(member.getId());

        WorkType workType;
        String address;
        int carCommuteTime;
        int trafficCommuteTime;
        double distance;
        int recentLossCount;
        int commuteTimeDifference;

        if (weightDetail.getWorkType() == null) {
            throw new ExceptionHandler(ErrorStatus.WORK_TYPE_NOT_FOUND);
        } else {
            workType = weightDetail.getWorkType();
        }

        if (weightDetail.getAddress() == null) {
            throw new ExceptionHandler(ErrorStatus.ADDRESS_NOT_FOUND);
        } else {
            //주소
            address = weightDetail.getAddress();

            // 대중교통 통근시간
            trafficCommuteTime = weightDetail.getTrafficCommuteTime();

            // 자가용 통근시간
            carCommuteTime = weightDetail.getCarCommuteTime();

            // 대중교통시간 - 자가용 통근시간
            commuteTimeDifference = Math.abs(trafficCommuteTime - carCommuteTime);

            //거리
            distance = weightDetail.getDistance();
        }

        // 연속낙첨횟수
        recentLossCount = weightDetail.getRecentLossCount();

        return toGetMemberWeightDTO(workType, carCommuteTime, trafficCommuteTime, distance, recentLossCount, address, commuteTimeDifference);
    }
}
