package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.*;
import com.cruise.parkinglotto.service.certificateDocsService.CertificateDocsService;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.converter.CertificateDocsConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final DrawRepository drawRepository;
    private final ApplicantRepository applicantRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final MemberRepository memberRepository;
    private final CertificateDocsRepository certificateDocsRepository;
    private final WeightDetailsRepository weightDetailsRepository;
    private final DrawService drawService;
    private final ObjectStorageService objectStorageService;
    private final ObjectStorageConfig objectStorageConfig;
    private final CertificateDocsService certificateDocsService;

    @Override
    @Transactional(readOnly = true)
    public Page<Applicant> getApplicantList(Integer page, Long drawId) {
        drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Page<Applicant> applicantList = applicantRepository.findByDrawId(PageRequest.of(page, 5), drawId);
        return applicantList;
    }

    @Override
    @Transactional
    public ApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        ParkingSpace parkingSpace = parkingSpaceRepository.findParkingSpaceByDrawId(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));
        parkingSpace.decrementSlots();
        applicant.approveParkingSpaceToPriority(parkingSpace.getId(), WinningStatus.WINNER, 0);
        return ApplicantConverter.toApprovePriorityResultDTO(parkingSpace);
    }

    @Override
    @Transactional
    public void drawApply(List<MultipartFile> certificateDocuments, ApplicantRequestDTO.GeneralApplyDrawRequestDTO applyDrawRequestDTO, String accountId) {
        Draw draw = drawRepository.findById(applyDrawRequestDTO.getDrawId()).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        if (draw.getStatus() != DrawStatus.OPEN) {
            throw new ExceptionHandler(ErrorStatus.DRAW_NOT_IN_APPLY_PERIOD);
        }

        Optional<Member> memberLoginOptional = memberRepository.findByAccountId(accountId);
        if (memberLoginOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        Member member = memberLoginOptional.get();

        //Handle Duplicated Applicant
        Optional<Applicant> applicantOptional = applicantRepository.findByDrawIdAndMemberId(draw.getId(), member.getId());
        if (applicantOptional.isPresent()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_DUPLICATED_APPLY);
        }

        //Handling userSeed when drawType is general
        String userSeed = applyDrawRequestDTO.getUserSeed();
        if (userSeed.length() > 1) {
            throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_TOO_LONG_USER_SEED);
        }

        //Handling carNum
        String carNum = applyDrawRequestDTO.getCarNum();
        memberRepository.updateCarNum(member.getId(), carNum);

        //Handling CertFile

        //업로드 할 파일 검증
        if (certificateDocuments != null) {
            certificateDocsService.validateCertificateFiles(certificateDocuments);
        }

        //deleteCertFiles
        List<CertificateDocsRequestDTO.CertifiCateFileDTO> deleteCertificatFileUrlAndNameDTO = applyDrawRequestDTO.getDeleteCertFileUrlAndNameDTO();

        //지울 정보(fileUrl)가 버킷에 있는지 확인 및 mysql에 있는 지울 정보 삭제
        if (deleteCertificatFileUrlAndNameDTO != null) {
            certificateDocsService.checkCertificateFileUrlsInBucket(deleteCertificatFileUrlAndNameDTO);
            certificateDocsService.deleteCertificateDocsInMySql(deleteCertificatFileUrlAndNameDTO);

            //버킷에서 정보 삭제
            for (CertificateDocsRequestDTO.CertifiCateFileDTO fileDTO : deleteCertificatFileUrlAndNameDTO) {
                String fileUrl = fileDTO.getFileUrl();
                objectStorageService.deleteObject(fileUrl);
            }
        }

        //uploadCertFiles
        if (certificateDocuments != null) {
            for (MultipartFile certificateDocument : certificateDocuments) {
                String fileName = certificateDocument.getOriginalFilename();
                String fileUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), fileName, certificateDocument);
                CertificateDocs certificateDocs = CertificateDocsConverter.toCertificateDocument(fileUrl, fileName, member);
                certificateDocsRepository.save(certificateDocs);
            }
        }

        //Handling address
        String address = applyDrawRequestDTO.getAddress();
        Integer carCommuteTime = applyDrawRequestDTO.getCarCommuteTime();
        Integer trafficCommuteTime = applyDrawRequestDTO.getTrafficCommuteTime();
        Double distance = applyDrawRequestDTO.getDistance();
        weightDetailsRepository.updateAddress(member, address);

        //Handling workType
        WorkType workType = applyDrawRequestDTO.getWorkType();

        //recentLossCount
        Optional<WeightDetails> weightDetailsOptional = weightDetailsRepository.findByMemberId(member.getId());

        Integer recentLossCount;
        if (weightDetailsOptional.isEmpty()) {
            recentLossCount = 0;
        } else {
            WeightDetails weightDetails = weightDetailsOptional.get();
            recentLossCount = weightDetails.getRecentLossCount();
        }

        WinningStatus winningStatus = WinningStatus.PENDING;

        //applicant 저장
        Applicant applicant = ApplicantConverter.makeInitialApplicantObject(member, draw, winningStatus, userSeed, applyDrawRequestDTO.getFirstChoice(), applyDrawRequestDTO.getSecondChoice(), distance, workType, trafficCommuteTime, carCommuteTime, recentLossCount);
        Applicant toGetApplicantId = applicantRepository.save(applicant);

        //userSeedIndex 배정
        Integer maxUserSeedIndex = applicantRepository.findMaxUserSeedIndexByDraw(draw);
        Integer newUserSeedIndex = maxUserSeedIndex + 1;
        applicantRepository.updateUserSeedIndex(toGetApplicantId.getId(), newUserSeedIndex);

        //weight 계산 및 입력
        drawService.calculateWeight(applicant);

    }
}
