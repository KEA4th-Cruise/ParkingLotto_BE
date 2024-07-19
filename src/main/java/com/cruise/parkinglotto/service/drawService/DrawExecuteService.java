package com.cruise.parkinglotto.service.drawService;

import org.springframework.stereotype.Service;


@Service
public interface DrawExecuteService {
    //추첨의 시드 번호를 저장
    void updateSeedNum(Long drawId);
    //신청자들의 난수 생성 및 할당
    void assignRandomNumber(Long drawId, String seed);
}
