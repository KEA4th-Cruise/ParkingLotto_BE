package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsResponseDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public class ApplicantConverter {

    public static ApplicantResponseDTO.GetApplicantResultDTO toGetApplicantResultDTO(Applicant applicant) {
        Member member = applicant.getMember();
        return ApplicantResponseDTO.GetApplicantResultDTO.builder()
                .applicantId(applicant.getId())
                .memberId(member.getId())
                .deptPathName(member.getDeptPathName())
                .employeeNo(member.getEmployeeNo())
                .nameKo(member.getNameKo())
                .build();
    }

    public static ApplicantResponseDTO.GetApplicantListResultDTO toGetApplicantListResultDTO(Page<Applicant> applicantPage) {
        List<ApplicantResponseDTO.GetApplicantResultDTO> getApplicantResultDTOList = applicantPage.stream()
                .map(ApplicantConverter::toGetApplicantResultDTO).toList();
        return ApplicantResponseDTO.GetApplicantListResultDTO.builder()
                .applicantList(getApplicantResultDTOList)
                .isFirst(applicantPage.isFirst())
                .isLast(applicantPage.isLast())
                .totalElements(applicantPage.getTotalElements())
                .totalPage(applicantPage.getTotalPages())
                .listSize(applicantPage.getSize())
                .build();
    }

    public static ApplicantResponseDTO.GetMyApplyResultDTO toGetMyApplyResultDTO(Applicant applicant) {

        return ApplicantResponseDTO.GetMyApplyResultDTO.builder()
                .drawTitle(applicant.getDraw().getTitle())
                .drawStatisticsId(applicant.getDraw().getDrawStatistics().getId())
                .reserveNum(applicant.getReserveNum())
                .winningStatus(applicant.getWinningStatus())
                .parkingSpaceId(applicant.getParkingSpaceId())
                .build();
    }


    public static ApplicantResponseDTO.MyApplyInfoDTO toMyApplyInfoDTO(Applicant applicant ) {

        return ApplicantResponseDTO.MyApplyInfoDTO.builder()
                .parkingSpaceId(applicant.getParkingSpaceId())
                .drawTitle(applicant.getDraw().getTitle())
                .winningStatus(applicant.getWinningStatus())
                .startDate(applicant.getDraw().getUsageStartAt())
                .endDate(applicant.getDraw().getUsageEndAt())
                .build();
    }

    public static Applicant makeInitialApplicantObject(Member member, Draw draw, WinningStatus winningStatus, String userSeed, Long firstChoice, Long secondChoice, Double distance, WorkType workType, Integer trafficCommuteTime, Integer carCommuteTime, Integer recentLossCount) {
        return Applicant.builder()
                .member(member)
                .draw(draw)
                .winningStatus(winningStatus)
                .reserveNum(-1)
                .userSeed(userSeed)
                .firstChoice(firstChoice)
                .secondChoice(secondChoice)
                .distance(distance)
                .workType(workType)
                .trafficCommuteTime(trafficCommuteTime)
                .carCommuteTime(carCommuteTime)
                .recentLossCount(recentLossCount)
                .build();
    }

    public static Applicant makeInitialPriorityApplicantObject(Member member, Draw draw, WinningStatus winningStatus, Double distance, WorkType workType, Integer trafficCommuteTime, Integer carCommuteTime, Integer recentLossCount) {
        return Applicant.builder()
                .member(member)
                .draw(draw)
                .winningStatus(winningStatus)
                .reserveNum(-1)
                .distance(distance)
                .workType(workType)
                .trafficCommuteTime(trafficCommuteTime)
                .carCommuteTime(carCommuteTime)
                .recentLossCount(recentLossCount)
                .build();
    }

    public static ApplicantResponseDTO.GetMyApplyResultListDTO toGetMyApplyResultListDTO (Page<ApplicantResponseDTO.GetMyApplyResultDTO> applyResultDTOList) {
        List<ApplicantResponseDTO.GetMyApplyResultDTO> getApplicantResultDTOList = applyResultDTOList.stream()
                .map((d)-> (d)).toList();
        return ApplicantResponseDTO.GetMyApplyResultListDTO.builder()
                .appliedDrawList(getApplicantResultDTOList)
                .isFirst(applyResultDTOList.isFirst())
                .isLast(applyResultDTOList.isLast())
                .totalElements(applyResultDTOList.getTotalElements())
                .totalPage(applyResultDTOList.getTotalPages())
                .listSize(applyResultDTOList.getSize())
                .build();
    }

    public static ApplicantResponseDTO.getMyApplyInformationDTO toGetMyApplyInformationDTO(Applicant applicant, String carNum, String address, List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFilesDTO){
        return ApplicantResponseDTO.getMyApplyInformationDTO.builder()
                .carNum(carNum)
                .address(address)
                .workType(applicant.getWorkType())
                .firstChoice(applicant.getFirstChoice())
                .secondChoice(applicant.getSecondChoice())
                .userSeed(applicant.getUserSeed())
                .recentLossCount(applicant.getRecentLossCount())
                .certificateFiles(certificateFilesDTO)
                .build();
    }
}
