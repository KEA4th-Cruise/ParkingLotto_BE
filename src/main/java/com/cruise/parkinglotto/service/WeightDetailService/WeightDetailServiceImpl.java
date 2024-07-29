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
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailRequestDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cruise.parkinglotto.web.converter.WeightDetailConverter.toCalculateWeightResponseDTO;
import static com.cruise.parkinglotto.web.converter.WeightDetailConverter.toGetMemberWeightDTO;

@Service
@RequiredArgsConstructor
public class WeightDetailServiceImpl implements WeightDetailService{
    private final JwtUtils jwtUtils;
    private final MemberService memberService;
    private final WeightDetailsRepository weightDetailsRepository;

    //계산용 변수
    private static final int WORK_TYPE1_SCORE = 25;
    private static final int WORK_TYPE2_SCORE = 5;
    private static final int TRAFFIC_COMMUTE_MAX_SCORE = 30;
    private static final int TRAFFIC_COMMUTE_BASE_SCORE = 10;
    private static final int CAR_COMMUTE_MAX_SCORE = 5;
    private static final int COMMUTE_DIFF_MAX_SCORE = 5;
    private static final int DISTANCE_MAX_SCORE = 20;
    private static final int RECENT_LOSS_COUNT_BASE_SCORE = 10;
    private static final int RECENT_LOSS_COUNT_EXTRA_SCORE = 5;

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

    @Override
    public WeightDetailResponseDTO.CalculateWeightResponseDTO calculateWeight(WeightDetailRequestDTO.CalculateWeightRequestDTO calculateWeightRequestDTO) {

        WorkType workType = calculateWeightRequestDTO.getWorkType();
        double distance = calculateWeightRequestDTO.getDistance();
        int carCommuteTime = calculateWeightRequestDTO.getCarCommuteTime();
        int trafficCommuteTime = calculateWeightRequestDTO.getTrafficCommuteTime();
        int recentLossCount = calculateWeightRequestDTO.getRecentLossCount();

        double weight = 0;

        // 근무타입에 따른 점수 부여
        if (workType == WorkType.TYPE1) {
            weight += WORK_TYPE1_SCORE;
        } else if (workType == WorkType.TYPE2) {
            weight += WORK_TYPE2_SCORE;
        }

        // 대중교통 통근시간에 따른 점수 부여
        if (trafficCommuteTime < 60) {
            weight += TRAFFIC_COMMUTE_BASE_SCORE + 9 * (1 - Math.exp(-0.2 * trafficCommuteTime));
        } else {
            weight += TRAFFIC_COMMUTE_BASE_SCORE + (TRAFFIC_COMMUTE_MAX_SCORE - TRAFFIC_COMMUTE_BASE_SCORE)
                    * (1 - Math.exp(-0.05 * (trafficCommuteTime - 60)));
        }

        // 자가용 통근시간에 따른 점수 부여
        weight += CAR_COMMUTE_MAX_SCORE * (1 - Math.exp(-0.05 * carCommuteTime));

        // 대중교통시간 - 자가용 통근시간 차이에 따른 점수 부여
        long commuteTimeDiff = Math.abs(trafficCommuteTime - carCommuteTime);
        weight += COMMUTE_DIFF_MAX_SCORE * (1 - Math.exp(-0.05 * commuteTimeDiff));

        // 직선거리에 따른 점수 부여
        weight += DISTANCE_MAX_SCORE * (1 - Math.exp(-0.02 * distance));

        // 연속낙첨횟수에 따른 점수 부여
        if (recentLossCount < 4) {
            weight += RECENT_LOSS_COUNT_BASE_SCORE * (1 - Math.exp(-0.3 * recentLossCount));
        } else {
            weight += RECENT_LOSS_COUNT_BASE_SCORE + RECENT_LOSS_COUNT_EXTRA_SCORE
                    * (1 - Math.exp(-0.7 * (recentLossCount - 3)));
        }
        return toCalculateWeightResponseDTO(weight);
    }
}
