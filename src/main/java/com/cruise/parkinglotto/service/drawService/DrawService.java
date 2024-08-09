package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

public interface DrawService {
    //추첨을 실행
    void executeDraw(Long drawId) throws IOException, MessagingException, NoSuchAlgorithmException;

    //추첨의 시드 번호를 저장
    void updateSeedNum(Long drawId);

    //신청자들의 난수 생성 및 할당
    void assignRandomNumber(Long drawId, String seed);

    void handleDrawResults(Long drawId, List<Applicant> orderedApplicants) throws MessagingException, NoSuchAlgorithmException;

    void assignZones(Long drawId, List<Applicant> selectedWinners);

    void calculateWeight(Applicant applicant);

    void assignWaitListNumbers(List<Applicant> applicants);

    void deleteUnconfirmedDrawsAndParkingSpaces();

    List<Applicant> weightedRandomSelectionAll(List<Applicant> applicants, Random random);

    DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo(HttpServletRequest httpServletRequest, Long drawId);

    Page<Applicant> getDrawResult(HttpServletRequest httpServletRequest, Long drawId, Integer page);

    Draw createDraw(MultipartFile mapImage, DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO);

    DrawResponseDTO.ConfirmDrawCreationResultDTO confirmDrawCreation(Long drawId);

    DrawResponseDTO.SimulateDrawResponseDTO simulateDraw(Long drawId, String seedNum, Integer page);

    DrawResponseDTO.GetDrawOverviewResultDTO getDrawOverview(HttpServletRequest httpServletRequest);

    DrawResponseDTO.DrawResultExcelDTO getDrawResultExcel(Long drawId);

    DrawResponseDTO.GetDrawInfoDetailDTO getDrawInfoDetail(HttpServletRequest httpServletRequest, Long drawId);

    DrawResponseDTO.GetDrawListResultDTO getDrawList(String year, DrawType drawType);

    void assignReservedApplicant(Long drawId, Long winnerId) throws MessagingException, NoSuchAlgorithmException;

    DrawResponseDTO.GetYearsFromDrawListDTO getYearsFromDrawList();

    void adminCancelWinner(HttpServletRequest httpServletRequest, Long drawId, Long applicantId) throws MessagingException, NoSuchAlgorithmException;

    void selfCancelWinner(HttpServletRequest httpServletRequest, Long drawId) throws MessagingException, NoSuchAlgorithmException;

    void openDraw(Draw draw);

    void closeDraw(Draw draw);

    Page<DrawResponseDTO.GetAppliedDrawResultDTO> getAppliedDrawList(String memberId, Integer page);

}