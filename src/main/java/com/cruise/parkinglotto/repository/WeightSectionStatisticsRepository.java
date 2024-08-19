package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.WeightSectionStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeightSectionStatisticsRepository extends JpaRepository<WeightSectionStatistics, Long> {

    List<WeightSectionStatistics> findByDrawId(Long drawId);
}