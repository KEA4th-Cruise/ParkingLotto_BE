package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/apply")
public class ApplicantController {

    private final ApplicantService applicantService;
    @PostMapping("/cancel/{memberId}")
    public void cancelApply(@PathVariable Long memberId) {
        applicantService.giveUpMyWinning(memberId);
    }
}
