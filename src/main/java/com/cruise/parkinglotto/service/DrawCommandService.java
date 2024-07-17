package com.cruise.parkinglotto.service;

import com.cruise.parkinglotto.web.dto.drawDto.DrawRequestDto;
import com.cruise.parkinglotto.web.dto.drawDto.DrawResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface DrawCommandService {
    DrawResponseDto.GetCurrentDrawInfo getCurrentDrawInfo(HttpServletRequest httpServletRequest, DrawRequestDto.GetCurrentDrawInfo getCurrentDrawInfo);
}
