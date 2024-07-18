package com.cruise.parkinglotto.service;

import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface DrawCommandService {
    DrawResponseDTO.GetCurrentDrawInfo getCurrentDrawInfo(HttpServletRequest httpServletRequest, DrawRequestDTO.GetCurrentDrawInfo getCurrentDrawInfo);
}
