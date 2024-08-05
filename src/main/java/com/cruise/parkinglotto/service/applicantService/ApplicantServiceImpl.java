package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
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
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import com.cruise.parkinglotto.domain.ParkingSpace;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        Page<Applicant> applicantList = applicantRepository.findByDrawId(PageRequest.of(page, 5), drawId);
        return applicantList;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicantResponseDTO.GetMyApplyResultDTO> getApplyResultList(Long memberId, Integer page) {

        List<Applicant> findApplicants = applicantRepository.findApplicantListByMemberId(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        List<ApplicantResponseDTO.GetMyApplyResultDTO> applyResultDTOList = findApplicants.stream()
                .map(a -> ApplicantConverter.toGetMyApplyResultDTO(a))
                .collect(Collectors.toList());

        PageRequest pageRequest = PageRequest.of(page, 4);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), applyResultDTOList.size());
        Page<ApplicantResponseDTO.GetMyApplyResultDTO> result = new PageImpl<ApplicantResponseDTO.GetMyApplyResultDTO>(applyResultDTOList.subList(start, end), pageRequest, applyResultDTOList.size());

        return result;

    }

    @Override
    @Transactional(readOnly = true)
    public ApplicantResponseDTO.MyApplyInfoDTO getMyApplyInfo(Long memberId, Long drawId) {

        Applicant findApplicant = applicantRepository.findApplicantByMemberIdAndDrawId(memberId, drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        ParkingSpace findParkingSpace = parkingSpaceRepository.findById(findApplicant.getParkingSpaceId()).orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));
        return ApplicantConverter.toMyApplyInfoDTO(findApplicant, findParkingSpace);

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
            throw new ExceptionHandler(ErrorStatus.CERTIFICATEDOCS_TOO_MANY);
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
        WeightDetails weightDetails = weightDetailsRepository.findByMemberId(member.getId());
        weightDetails.updateWeightDetailsInApply(address, applyDrawRequestDTO.getWorkType(), trafficCommuteTime, carCommuteTime, distance);

        //Handling workType
        WorkType workType = applyDrawRequestDTO.getWorkType();

        //recentLossCount
        Optional<WeightDetails> weightDetailsOptional = weightDetailsRepository.findOptionalByMemberId(member.getId());

        Integer recentLossCount;
        if (weightDetailsOptional.isEmpty()) {
            recentLossCount = 0;
        } else {
            recentLossCount = weightDetails.getRecentLossCount();
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
    public ApplicantResponseDTO.GetApplicantResultDTO searchApplicantBySearchKeyword(String searchKeyword) {
        Applicant applicant = applicantRepository.findByApplicantEmployeeNoOrAccountId(searchKeyword).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_SEARCH_NOT_FOUND));
        return ApplicantConverter.toGetApplicantResultDTO(applicant);
    }

    @Override
    public ApplicantResponseDTO.GetApplicantResultDTO searchWinnerBySearchKeyword(String searchKeyword) {
        Applicant winner = applicantRepository.findByWinnerEmployeeNoOrAccountId(searchKeyword).orElseThrow(() -> new ExceptionHandler(ErrorStatus.WINNER_SEARCH_NOT_FOUND));
        return ApplicantConverter.toGetApplicantResultDTO(winner);
    }

}
