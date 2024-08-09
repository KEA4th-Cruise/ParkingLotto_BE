package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.*;
import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.global.sse.SseEmitters;
import com.cruise.parkinglotto.repository.*;
import com.cruise.parkinglotto.service.certificateDocsService.CertificateDocsService;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.converter.CertificateDocsConverter;
import com.cruise.parkinglotto.web.converter.WeightDetailConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.certificateDocsDTO.CertificateDocsRequestDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final SseEmitters sseEmitters;


    @Override
    @Transactional(readOnly = true)
    public Page<Applicant> getApplicantList(Integer page, Long drawId) {
        drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Page<Applicant> applicantList = applicantRepository.findByDrawId(PageRequest.of(page, 6), drawId);
        return applicantList;
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicantResponseDTO.MyApplyInfoDTO getMyApplyInfo(Long memberId, Long drawId) {

        Applicant findApplicant = applicantRepository.findApplicantByMemberIdAndDrawId(memberId, drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));

        return ApplicantConverter.toMyApplyInfoDTO(findApplicant);

    }

    @Override
    @Transactional
    public void drawApply(List<MultipartFile> certificateDocuments, ApplicantRequestDTO.GeneralApplyDrawRequestDTO applyDrawRequestDTO, String accountId, Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

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

        //Handling userSeed
        String userSeed = applyDrawRequestDTO.getUserSeed();
        if (userSeed.length() > 1) {
            throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_TOO_LONG_USER_SEED);
        }

        //파일 검증 ==========
        List<CertificateDocsRequestDTO.CertificateFileDTO> useProfileFileUrlDTO = applyDrawRequestDTO.getUseProfileFileUrlDTO();

        //프로파일에 있는 이름과 업로드할 파일의 이름이 동일하면 예외처리
        if (certificateDocuments != null && applyDrawRequestDTO.getUseProfileFileUrlDTO() != null) {
            certificateDocsService.prohibitSameFileNamesBetweenProfileFileUrlsAndMultiPartFiles(certificateDocuments, applyDrawRequestDTO.getUseProfileFileUrlDTO());
        }

        //한개의 추첨에서 들어갈 수 있는 파일 개수 세는 변수
        Integer totalFileNumber = 0;

        //업로드 할 파일 검증 (길이, 확장자 등)
        if (certificateDocuments != null) {
            certificateDocsService.validateCertificateFiles(certificateDocuments);
            totalFileNumber += certificateDocuments.size();
        }

        //쓸 유저 프로파일 파일의 url이 유효한지 확인
        if (useProfileFileUrlDTO != null) {
            certificateDocsService.checkCertificateFileUrlsInBucket(useProfileFileUrlDTO);
            totalFileNumber += useProfileFileUrlDTO.size();
        }

        if (totalFileNumber > 5) {
            throw new ExceptionHandler(ErrorStatus.FILE_TOO_MANY);
        }

        //파일 검증 끝 =========


        //Handling carNum
        String carNum = applyDrawRequestDTO.getCarNum();
        memberRepository.updateCarNum(member.getId(), carNum);

        //Handling CertFile

        //uploadCertFiles
        if (certificateDocuments != null) {
            for (MultipartFile certificateDocument : certificateDocuments) {
                String fileName = certificateDocument.getOriginalFilename();
                String addMemberIdAndDrawIdFileUrl = certificateDocsService.makeCertificateFileUrl(member.getId(), draw.getId(), DrawType.GENERAL, fileName);
                String fileUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), addMemberIdAndDrawIdFileUrl, certificateDocument);
                CertificateDocs certificateDocs = CertificateDocsConverter.toCertificateDocument(fileUrl, fileName, member, draw.getId());
                certificateDocsRepository.save(certificateDocs);
            }
        }


        if (useProfileFileUrlDTO != null) {
            for (CertificateDocsRequestDTO.CertificateFileDTO certificateFileDTO : useProfileFileUrlDTO) {
                String fileName = certificateFileDTO.getFileName();
                String fileUrl = certificateFileDTO.getFileUrl();
                CertificateDocs certificateDocs = CertificateDocsConverter.toCertificateDocument(fileUrl, fileName, member, draw.getId());
                certificateDocsRepository.save(certificateDocs);
            }
        }

        //Handling weightDetails
        String address = applyDrawRequestDTO.getAddress();
        Integer carCommuteTime = applyDrawRequestDTO.getCarCommuteTime();
        Integer trafficCommuteTime = applyDrawRequestDTO.getTrafficCommuteTime();
        Double distance = applyDrawRequestDTO.getDistance();

        //Handling workType
        WorkType workType = applyDrawRequestDTO.getWorkType();

        //recentLossCount
        Optional<WeightDetails> weightDetailsOptional = weightDetailsRepository.findOptionalByMemberId(member.getId());

        Integer recentLossCount = 0;

        if (weightDetailsOptional.isEmpty()) {
            WeightDetails weightDetails = WeightDetailConverter.makeWeightDetails(member, address, workType, trafficCommuteTime, carCommuteTime, distance);
            weightDetailsRepository.save(weightDetails);
        } else {
            WeightDetails weightDetails = weightDetailsOptional.get();
            recentLossCount = weightDetails.getRecentLossCount();
            weightDetails.updateWeightDetailsInApply(address, applyDrawRequestDTO.getWorkType(), trafficCommuteTime, carCommuteTime, distance);
        }

        WinningStatus winningStatus = WinningStatus.PENDING;

        //applicant 저장
        Applicant applicant = ApplicantConverter.makeInitialApplicantObject(member, draw, winningStatus, userSeed, applyDrawRequestDTO.getFirstChoice(), applyDrawRequestDTO.getSecondChoice(), distance, workType, trafficCommuteTime, carCommuteTime, recentLossCount);
        Applicant toGetApplicantId = applicantRepository.save(applicant);

        //userSeedIndex 배정
        Integer maxUserSeedIndex = applicantRepository.findMaxUserSeedIndexByDraw(draw);
        Integer newUserSeedIndex = maxUserSeedIndex + 1;
        toGetApplicantId.updateUserSeedIndex(newUserSeedIndex);

        //주차장 자리 업데이트
        ParkingSpace parkingSpace = parkingSpaceRepository.findUserCountWithDrawAndFirstChoice(applyDrawRequestDTO.getFirstChoice(), drawId);
        parkingSpace.updateApplicantCount();

        //weight 계산 및 입력
        drawService.calculateWeight(applicant);

        //  SSE로 실시간 데이터 전송 (비동기)
        sseEmitters.realTimeDrawInfo(draw);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Applicant> searchApplicant(Integer page, String keyword, Long drawId) {

        return applicantRepository.findApplicantByDrawIdAndKeyword(PageRequest.of(page, 6), keyword, drawId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Applicant> searchWinner(Integer page, String keyword, Long drawId) {

        return applicantRepository.findWinnerByDrawIdAndKeyword(PageRequest.of(page, 6), keyword, drawId);
    }

    @Override
    public void cancelApply(String accountId, Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        if (draw.getStatus() != DrawStatus.OPEN) {
            throw new ExceptionHandler(ErrorStatus.DRAW_NOT_IN_APPLY_PERIOD);
        }

        Optional<Member> memberLoginOptional = memberRepository.findByAccountId(accountId);
        if (memberLoginOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        Member member = memberLoginOptional.get();

        //Handle Applicant is present
        Optional<Applicant> applicantOptional = applicantRepository.findByDrawIdAndMemberId(draw.getId(), member.getId());
        if (applicantOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }

        Applicant applicant = applicantOptional.get();
        Optional<ParkingSpace> parkingSpaceOptional = parkingSpaceRepository.findById(applicant.getFirstChoice());
        if (parkingSpaceOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND);
        }
        ParkingSpace parkingSpace = parkingSpaceOptional.get();

        parkingSpace.decreaseApplicantCount();

        //bucket에서 문서 삭제
        List<CertificateDocs> certificateDocs = certificateDocsRepository.findByMemberAndDrawId(member, drawId);

        if (!objectStorageService.doesObjectCertificateFileUrlsExist(certificateDocs)) {
            throw new ExceptionHandler(ErrorStatus.FILE_NAME_NOT_FOUND);
        }

        certificateDocsService.deleteFileIsNotInProfile(certificateDocs);

        //DB에서 문서정보 삭제
        certificateDocsRepository.deleteAll(certificateDocs);

        applicantRepository.deleteByDrawIdAndMember(drawId, member);

    }


    @Override
    @Transactional
    public ApplicantResponseDTO.getMyApplyInformationDTO getMyApplyInformation(Long drawId, String accountId) {
        Optional<Member> memberOptional = memberRepository.findByAccountId(accountId);
        Member member = memberOptional.get();

        //신청자 존재 확인
        Optional<Applicant> applicantOptional = applicantRepository.findByDrawIdAndMemberId(drawId, member.getId());
        if (applicantOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }
        Applicant applicant = applicantOptional.get();

        WeightDetails weightDetails = weightDetailsRepository.findByMemberId(member.getId());

        List<CertificateDocs> fileDTOList = certificateDocsRepository.findByMemberAndDrawId(member, drawId);
        List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFileDTOS = fileDTOList.stream()
                .map(CertificateDocsConverter::toCertificateFileDTO)
                .collect(Collectors.toList());
        return ApplicantConverter.toGetMyApplyInformationDTO(applicant, weightDetails.getAddress(), member.getCarNum(), certificateFileDTOS);
    }
}
