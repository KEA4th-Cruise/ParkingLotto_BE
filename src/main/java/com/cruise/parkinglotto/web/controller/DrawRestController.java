package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.service.drawService.DrawExecuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draw")
@RequiredArgsConstructor
public class DrawRestController {
    private final DrawExecuteService drawExecuteService;

}