package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
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


    @Override
    @Transactional(readOnly = true)
    public Page<Applicant> getApplicantList(Integer page, Long drawId) {
        drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Page<Applicant> applicantList = applicantRepository.findByDrawId(PageRequest.of(page, 5), drawId);
        return applicantList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicantResponseDTO.GetMyApplyResultDTO> getApplyResultList(Long memberId) {

        List<Applicant> findApplicants = applicantRepository.findApplicantListByMemberId(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        List<ApplicantResponseDTO.GetMyApplyResultDTO> result = findApplicants.stream()
                .map(a -> ApplicantConverter.toGetMyApplyResultDTO(a))
                .collect(Collectors.toList());

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

        //Handling userSeed
        String userSeed = applyDrawRequestDTO.getUserSeed();
        if (userSeed.length() > 1) {
            throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_TOO_LONG_USER_SEED);
        }

        //프로파일에 있는 이름과 업로드할 파일의 이름이 동일하면 예외처리
        if(certificateDocuments!=null && applyDrawRequestDTO.getUseProfileFileUrlDTO()!=null){
            certificateDocsService.prohibitSameFileNamesBetweenProfileFileUrlsAndMultiPartFiles(certificateDocuments, applyDrawRequestDTO.getUseProfileFileUrlDTO());
        }

        //업로드 할 파일 검증
        if (certificateDocuments != null) {
            certificateDocsService.validateCertificateFiles(certificateDocuments);
        }

        //Handling carNum
        String carNum = applyDrawRequestDTO.getCarNum();
        memberRepository.updateCarNum(member.getId(), carNum);

        //Handling CertFile

        //uploadCertFiles
        if (certificateDocuments != null) {
            for (MultipartFile certificateDocument : certificateDocuments) {
                String fileName = certificateDocument.getOriginalFilename();
                String addMemberIdAndDrawIdFileUrl = member.getId().toString() + "_" + draw.getId().toString() + "_" + fileName;
                String fileUrl = objectStorageService.uploadObject(objectStorageConfig.getGeneralCertificateDocument(), addMemberIdAndDrawIdFileUrl, certificateDocument);
                CertificateDocs certificateDocs = CertificateDocsConverter.toCertificateDocument(fileUrl, fileName, member, draw.getId());
                certificateDocsRepository.save(certificateDocs);
            }
        }

        List<CertificateDocsRequestDTO.CertificateFileDTO> useProfileFileUrlDTO = applyDrawRequestDTO.getUseProfileFileUrlDTO();
        if(useProfileFileUrlDTO!=null){
            for (CertificateDocsRequestDTO.CertificateFileDTO certificateFileDTO : useProfileFileUrlDTO) {
                String fileName = certificateFileDTO.getFileName();
                String fileUrl = certificateFileDTO.getFileUrl();
                CertificateDocs certificateDocs = CertificateDocsConverter.toCertificateDocument(fileUrl, fileName, member, draw.getId());
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
        Optional<WeightDetails> weightDetailsOptional = weightDetailsRepository.findOptionalByMemberId(member.getId());

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
