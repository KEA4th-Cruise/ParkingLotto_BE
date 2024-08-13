package com.cruise.parkinglotto.service.drawStatisticsService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.DrawStatisticsRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.repository.WeightSectionStatisticsRepository;
import com.cruise.parkinglotto.web.converter.DrawStatisticsConverter;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrawStatisticsServiceImpl implements DrawStatisticsService {

    private final DrawRepository drawRepository;
    private final DrawStatisticsRepository drawStatisticsRepository;
    private final WeightSectionStatisticsRepository weightSectionStatisticsRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;


    @Override
    @Transactional(readOnly = true)
    public List<DrawStatistics> getRecentDrawStatistics() {
        List<Draw> drawList = drawRepository.findTop5ByTypeAndStatusOrderByUsageStartAtDesc(DrawType.GENERAL, DrawStatus.COMPLETED);
        drawList.sort(Comparator.comparing(Draw::getUsageStartAt));
        List<DrawStatistics> drawStatisticsList = drawList.stream()
                .map(Draw::getDrawStatistics)
                .toList();
        return drawStatisticsList;
    }

    @Override
    @Transactional
    public void updateDrawStatistics(Long drawId, List<Applicant> applicants) {
        Draw draw = drawRepository.findById(drawId).orElseThrow();
        int totalSlots = draw.getTotalSlots();
        List<Applicant> winners = applicants.stream()
                .filter(applicant -> applicant.getWinningStatus() == WinningStatus.WINNER)
                .toList();
        // 당첨자 평균값 계산
        double trafficCommuteTimeAvg = winners.stream()
                .mapToDouble(Applicant::getTrafficCommuteTime)
                .average()
                .orElse(0.0);

        double carCommuteTimeAvg = winners.stream()
                .mapToDouble(Applicant::getCarCommuteTime)
                .average()
                .orElse(0.0);

        double distanceAvg = winners.stream()
                .mapToDouble(Applicant::getDistance)
                .average()
                .orElse(0.0);

        double recentLossCountAvg = winners.stream()
                .mapToDouble(Applicant::getRecentLossCount)
                .average()
                .orElse(0.0);

        double winnersWeightAvg = winners.stream()
                .mapToDouble(Applicant::getWeightedTotalScore)
                .average()
                .orElse(0.0);

        // WORKTYPE 개수 세기
        long type1Count = winners.stream()
                .filter(applicant -> applicant.getWorkType() == WorkType.TYPE1)
                .count();

        long type2Count = winners.stream()
                .filter(applicant -> applicant.getWorkType() == WorkType.TYPE2)
                .count();

        WorkType dominantWorkType = (type1Count >= type2Count) ? WorkType.TYPE1 : WorkType.TYPE2;

        DrawStatistics drawStatistics = DrawStatisticsConverter.toDrawStatistics(draw, applicants, totalSlots, trafficCommuteTimeAvg, carCommuteTimeAvg, distanceAvg, recentLossCountAvg, winnersWeightAvg, dominantWorkType);
        drawStatisticsRepository.save(drawStatistics);
    }

    @Override
    @Transactional(readOnly = true)
    public DrawStatisticsResponseDTO.GetDrawStatisticsResultDTO getDrawStatistics(Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        List<ParkingSpace> parkingSpaceList = parkingSpaceRepository.findByDrawId(draw.getId());
        List<WeightSectionStatistics> weightSectionStatisticsList = weightSectionStatisticsRepository.findByDrawId(draw.getId());
        String drawTitle = draw.getTitle();
        Integer totalSlots = draw.getTotalSlots();
        DrawStatistics drawStatistics = draw.getDrawStatistics();
        return DrawStatisticsConverter.toGetDrawStatisticsResultDTO(drawTitle, drawStatistics, totalSlots, parkingSpaceList, weightSectionStatisticsList);
    }

}