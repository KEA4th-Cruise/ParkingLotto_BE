package com.cruise.parkinglotto.service.priorityApplicantService;

import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.CertificateDocsRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.repository.PriorityApplicantRepository;
import com.cruise.parkinglotto.web.converter.PriorityApplicantConverter;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PriorityApplicantServiceImpl implements PriorityApplicantService {

    private final DrawRepository drawRepository;
    private final PriorityApplicantRepository priorityApplicantRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final CertificateDocsRepository certificateDocsRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PriorityApplicant> getPriorityApplicantList(Integer page, Long drawId, ApprovalStatus approvalStatus) {
        drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Page<PriorityApplicant> priorityApplicantList = priorityApplicantRepository.findByDrawIdAndApprovalStatus(PageRequest.of(page, 5), drawId, approvalStatus);
        return priorityApplicantList;
    }

    @Override
    @Transactional
    public PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long priorityApplicantId) {
        PriorityApplicant priorityApplicant = priorityApplicantRepository.findById(priorityApplicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        ParkingSpace parkingSpace = parkingSpaceRepository.findParkingSpaceByDrawId(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));
        parkingSpace.decrementSlots();
        priorityApplicant.approveParkingSpaceToPriority(parkingSpace.getId(), ApprovalStatus.APPROVED);
        return PriorityApplicantConverter.toApprovePriorityResultDTO(parkingSpace);
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
}