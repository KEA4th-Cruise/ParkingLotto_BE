package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;

import java.util.List;
import java.util.Random;

public interface DrawService {
    //추첨을 실행
    void executeDraw(Long drawId);
    //추첨의 시드 번호를 저장
    void updateSeedNum(Long drawId);
    //신청자들의 난수 생성 및 할당
    void assignRandomNumber(Long drawId, String seed);
    //당첨자 뽑기
    List<Applicant> selectWinners(Long drawId, List<Applicant> applicants, Random random);

    void assignZones(Long drawId, List<Applicant> selectedWinners);

    void calculateWeight(Applicant applicant);

    void assignWaitListNumbers(List<Applicant> applicants);
}
