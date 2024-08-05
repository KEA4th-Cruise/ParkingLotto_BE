package com.cruise.parkinglotto.service.drawStatisticsService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WeightSection;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.*;
import com.cruise.parkinglotto.web.converter.DrawStatisticsConverter;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DrawStatisticsServiceImpl implements DrawStatisticsService {

    private final DrawRepository drawRepository;
    private final ApplicantRepository applicantRepository;
    private final DrawStatisticsRepository drawStatisticsRepository;
    private final WeightSectionStatisticsRepository weightSectionStatisticsRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;


    @Override
    @Transactional(readOnly = true)
    public List<DrawStatistics> getRecentDrawStatistics() {
        List<Draw> drawList = drawRepository.findTop5ByTypeOrderByUsageStartAtDesc(DrawType.GENERAL);
        List<DrawStatistics> drawStatisticsList = drawList.stream()
                .map(Draw::getDrawStatistics)
                .toList();
        return drawStatisticsList;
    }

    @Override
    @Transactional
    public void updateDrawStatistics(Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow();
        int totalSlots = draw.getTotalSlots();
        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
        DrawStatistics drawStatistics = DrawStatisticsConverter.toDrawStatistics(draw, applicants, totalSlots);
        drawStatisticsRepository.save(drawStatistics);
    }

    @Override
    @Transactional(readOnly = true)
    public DrawStatisticsResponseDTO.GetDrawStatisticsResultDTO getDrawStatistics(Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        List<ParkingSpace> parkingSpaceList = parkingSpaceRepository.findByDrawId(draw.getId());
        List<WeightSectionStatistics> weightSectionStatisticsList = weightSectionStatisticsRepository.findByDrawId(draw.getId());
        Integer applicantCount = draw.getDrawStatistics().getTotalApplicants();
        Integer totalSlots = draw.getTotalSlots();
        return DrawStatisticsConverter.toGetDrawStatisticsResultDTO(applicantCount, totalSlots, parkingSpaceList, weightSectionStatisticsList);
    }

}
