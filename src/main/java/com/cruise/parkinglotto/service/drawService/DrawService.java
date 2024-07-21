package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;

public interface DrawService {
    //추첨을 실행
    void executeDraw(Long drawId);
    //추첨의 시드 번호를 저장
    void updateSeedNum(Long drawId);
    //신청자들의 난수 생성 및 할당
    void assignRandomNumber(Long drawId, String seed);

    void handleDrawResults(Long drawId, List<Applicant> orderedApplicants);

    void assignZones(Long drawId, List<Applicant> selectedWinners);

    void calculateWeight(Applicant applicant);

    void assignWaitListNumbers(List<Applicant> applicants);

    List<Applicant> weightedRandomSelectionAll(List<Applicant> applicants, Random random);

    DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo(HttpServletRequest httpServletRequest, Long drawId);

    Draw createDraw(MultipartFile mapImage, DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO);
}