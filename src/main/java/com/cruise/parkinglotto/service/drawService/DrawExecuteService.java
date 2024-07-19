package com.cruise.parkinglotto.service.drawService;

import org.springframework.stereotype.Service;


@Service
public interface DrawExecuteService {
    void updateSeedNum(Long drawId);
}
