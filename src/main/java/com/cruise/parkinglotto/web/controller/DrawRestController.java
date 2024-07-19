package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.service.drawService.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draw")
@RequiredArgsConstructor
public class DrawRestController {
    private final DrawService drawService;

}