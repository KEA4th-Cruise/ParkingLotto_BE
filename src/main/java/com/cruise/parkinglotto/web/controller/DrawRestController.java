package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.service.drawService.DrawExecuteService;
import com.cruise.parkinglotto.web.dto.DrawResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draw")
@RequiredArgsConstructor
public class DrawRestController {
    private final DrawExecuteService drawExecuteService;

}