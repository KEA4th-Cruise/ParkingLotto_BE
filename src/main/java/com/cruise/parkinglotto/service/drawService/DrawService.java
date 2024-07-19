package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Random;

public interface DrawService {
    DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo(HttpServletRequest httpServletRequest, DrawRequestDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo);
}
