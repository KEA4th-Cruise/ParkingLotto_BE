package com.cruise.parkinglotto.service.priorityApplicantService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.*;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.*;
import com.cruise.parkinglotto.service.certificateDocsService.CertificateDocsService;
import com.cruise.parkinglotto.web.converter.CertificateDocsConverter;
import com.cruise.parkinglotto.web.converter.PriorityApplicantConverter;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriorityApplicantServiceImpl implements PriorityApplicantService {

    private final DrawRepository drawRepository;
    private final PriorityApplicantRepository priorityApplicantRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final CertificateDocsRepository certificateDocsRepository;
    private final MemberRepository memberRepository;
    private final CertificateDocsService certificateDocsService;
    private final ObjectStorageService objectStorageService;
    private final ObjectStorageConfig objectStorageConfig;

    @Override
    @Transactional(readOnly = true)
    public PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO getPriorityApplicantList(Integer page, Long drawId, ApprovalStatus approvalStatus) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Integer totalSlots = draw.getTotalSlots();
        Page<PriorityApplicant> priorityApplicantList = priorityApplicantRepository.findByDrawIdAndApprovalStatus(PageRequest.of(page, 6), drawId, approvalStatus);
        return PriorityApplicantConverter.toGetPriorityApplicantListResultDTO(totalSlots, priorityApplicantList);
    }

    @Override
    @Transactional
    public PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long priorityApplicantId) {
        PriorityApplicant priorityApplicant = priorityApplicantRepository.findById(priorityApplicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        priorityApplicant.approvePriority(ApprovalStatus.APPROVED);
        return PriorityApplicantConverter.toApprovePriorityResultDTO(priorityApplicant);
    }

    @Override
    @Transactional(readOnly = true)
    public PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO getPriorityApplicantDetails(Long drawId, Long priorityApplicantId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        PriorityApplicant priorityApplicant = priorityApplicantRepository.findById(priorityApplicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        if (!Objects.equals(draw.getId(), priorityApplicant.getDraw().getId())) {
            throw new ExceptionHandler(ErrorStatus.DRAW_MISMATCH);
        }
        List<CertificateDocs> certificateDocsList = certificateDocsRepository.findByMemberAndDrawId(priorityApplicant.getMember(), priorityApplicant.getDraw().getId());
        return PriorityApplicantConverter.toGetPriorityApplicantDetailsResultDTO(priorityApplicant, certificateDocsList);
    }

    @Override
    @Transactional
    public PriorityApplicantResponseDTO.RejectPriorityResultDTO rejectPriority(Long drawId, Long priorityApplicantId) {
        PriorityApplicant priorityApplicant = priorityApplicantRepository.findById(priorityApplicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        priorityApplicant.rejectPriorityApply(ApprovalStatus.REJECTED);
        return PriorityApplicantConverter.toRejectPriorityResultDTO(priorityApplicant);
    }

    @Override
    @Transactional
    public void drawPriorityApply(List<MultipartFile> generalCertificateDocs, List<MultipartFile> priorityCertificateDocs, PriorityApplicantRequestDTO.PriorityApplyDrawRequestDTO priorityApplyDrawRequestDTO, String accountId, Long drawId) {
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
        Optional<PriorityApplicant> priorityApplicantOptional = priorityApplicantRepository.findByDrawIdAndMemberId(draw.getId(), member.getId());
        if (priorityApplicantOptional.isPresent()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_DUPLICATED_APPLY);
        }

        //파일 검증 ==========
        List<CertificateDocsRequestDTO.CertificateFileDTO> useProfileFileUrlDTO = priorityApplyDrawRequestDTO.getUseProfileFileUrlDTO();

        //프로파일에 있는 이름과 업로드할 파일의 이름이 동일하면 예외처리
        if (generalCertificateDocs != null && priorityApplyDrawRequestDTO.getUseProfileFileUrlDTO() != null) {
            certificateDocsService.prohibitSameFileNamesBetweenProfileFileUrlsAndMultiPartFiles(generalCertificateDocs, priorityApplyDrawRequestDTO.getUseProfileFileUrlDTO());
        }

        //한개의 추첨에서 들어갈 수 있는 파일 개수 세는 변수
        Integer totalGeneralFileNumber = 0;

        //업로드 할 파일 검증 (길이, 확장자 등)
        if (generalCertificateDocs != null) {
            certificateDocsService.validateCertificateFiles(generalCertificateDocs);
            totalGeneralFileNumber += generalCertificateDocs.size();
        }

        //쓸 유저 프로파일 파일의 url이 유효한지 확인
        if (useProfileFileUrlDTO != null) {
            certificateDocsService.checkCertificateFileUrlsInBucket(useProfileFileUrlDTO);
            totalGeneralFileNumber += useProfileFileUrlDTO.size();
        }

        if (totalGeneralFileNumber > 5) {
            throw new ExceptionHandler(ErrorStatus.FILE_TOO_MANY);
        }

        //우대 서류 검증 (확장자 및 길이, 개수)
        if (priorityCertificateDocs.size() > 5) {
            throw new ExceptionHandler(ErrorStatus.FILE_TOO_MANY);
        }

        certificateDocsService.validateCertificateFiles(priorityCertificateDocs);

        //파일 검증 끝 =========


        //Handling carNum
        String carNum = priorityApplyDrawRequestDTO.getCarNum();
        memberRepository.updateCarNum(member.getId(), carNum);

        //Handling CertFile

        //uploadCertFiles
        if (generalCertificateDocs != null) {
            for (MultipartFile certificateDocument : generalCertificateDocs) {
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

        //Priority
        for (MultipartFile certificateDocument : priorityCertificateDocs) {
            String fileName = certificateDocument.getOriginalFilename();
            String addMemberIdAndDrawIdFileUrl = certificateDocsService.makeCertificateFileUrl(member.getId(), draw.getId(), DrawType.PRIORITY, fileName);
            String fileUrl = objectStorageService.uploadObject(objectStorageConfig.getPriorityCertificateDocument(), addMemberIdAndDrawIdFileUrl, certificateDocument);
            CertificateDocs certificateDocs = CertificateDocsConverter.toCertificateDocument(fileUrl, fileName, member, draw.getId());
            certificateDocsRepository.save(certificateDocs);
        }

        //applicant 저장
        PriorityApplicant priorityApplicant = PriorityApplicantConverter.makeInitialPriorityApplicantObject(member, draw, carNum);
        priorityApplicantRepository.save(priorityApplicant);

    }

    @Override
    @Transactional
    public PriorityApplicantResponseDTO.AssignPriorityResultListDTO assignPriority(Long drawId) {
        List<ParkingSpace> parkingSpaceList = parkingSpaceRepository.findByDrawId(drawId);
        List<PriorityApplicant> priorityApplicantList = priorityApplicantRepository.findApprovedApplicantsByDrawId(drawId, ApprovalStatus.APPROVED);
        if (priorityApplicantList.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPROVED_APPLICANTS_NOT_EXIST);
        }
        Collections.shuffle(priorityApplicantList);
        for (PriorityApplicant applicant : priorityApplicantList) {
            boolean assigned = false;
            for (ParkingSpace parkingSpace : parkingSpaceList) {
                if (parkingSpace.getRemainSlots() > 0) {
                    applicant.assignParkingSpace(parkingSpace.getId());
                    parkingSpace.decrementSlots();
                    assigned = true;
                    break;
                }
            }
            if (!assigned) {
                throw new ExceptionHandler(ErrorStatus.NO_REMAIN_SLOTS);
            }
        }
        drawRepository.updateStatus(drawId, DrawStatus.COMPLETED);
        return PriorityApplicantConverter.toAssignPriorityResultListDTO(priorityApplicantList);
    }

    @Override
    @Transactional
    public void cancelPriorityApply(String accountId, Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        if (draw.getStatus() != DrawStatus.OPEN) {
            throw new ExceptionHandler(ErrorStatus.DRAW_NOT_IN_APPLY_PERIOD);
        }

        Optional<Member> memberLoginOptional = memberRepository.findByAccountId(accountId);
        if (memberLoginOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        Member member = memberLoginOptional.get();

        //Handle PriorityApplicant is present
        Optional<PriorityApplicant> priorityApplicantOptional = priorityApplicantRepository.findByDrawIdAndMemberId(draw.getId(), member.getId());
        if (priorityApplicantOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }

        //bucket에서 문서 삭제
        List<CertificateDocs> certificateDocs = certificateDocsRepository.findByMemberAndDrawId(member, drawId);

        if (!objectStorageService.doesObjectCertificateFileUrlsExist(certificateDocs)) {
            throw new ExceptionHandler(ErrorStatus.FILE_NAME_NOT_FOUND);
        }

        certificateDocsService.deleteFileIsNotInProfile(certificateDocs);

        //DB에서 문서정보 삭제
        certificateDocsRepository.deleteAll(certificateDocs);

        priorityApplicantRepository.deleteByDrawIdAndMember(drawId, member);

    }

    @Override
    @Transactional
    public PriorityApplicantResponseDTO.CancelPriorityAssignResultDTO cancelPriorityAssign(Long drawId, Long priorityApplicantId) {
        PriorityApplicant priorityApplicant = priorityApplicantRepository.findById(priorityApplicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        if (!priorityApplicant.getApprovalStatus().equals(ApprovalStatus.ASSIGNED)) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_ASSIGNED);
        } else {
            priorityApplicant.cancelPriorityAssign();
            return PriorityApplicantConverter.toCancelPriorityAssignmentResultDTO(priorityApplicant);
        }
    }
}