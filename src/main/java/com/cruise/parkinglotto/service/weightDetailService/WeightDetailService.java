package com.cruise.parkinglotto.service.weightDetailService;

import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailRequestDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface WeightDetailService {
    WeightDetailResponseDTO.GetMemberWeightDTO getMemberWeight(HttpServletRequest httpServletRequest);

    WeightDetailResponseDTO.CalculateWeightResponseDTO calculateWeight(WeightDetailRequestDTO.CalculateWeightRequestDTO calculateWeightRequestDTO);
}
