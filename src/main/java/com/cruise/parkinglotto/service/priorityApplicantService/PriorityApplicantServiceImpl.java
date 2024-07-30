package com.cruise.parkinglotto.service.priorityApplicantService;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.repository.PriorityApplicantRepository;
import com.cruise.parkinglotto.web.converter.PriorityApplicantConverter;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PriorityApplicantServiceImpl implements PriorityApplicantService {

    private final DrawRepository drawRepository;
    private final PriorityApplicantRepository priorityApplicantRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PriorityApplicant> getPriorityApplicantList(Integer page, Long drawId, ApprovalStatus approvalStatus) {
        drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Page<PriorityApplicant> priorityApplicantList = priorityApplicantRepository.findPriorityApplicantPageByDrawIdAndApprovalStatus(PageRequest.of(page, 5), drawId, approvalStatus);
        return priorityApplicantList;
    }

    @Override
    @Transactional
    public PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriority(Long drawId, Long priorityApplicantId) {
        PriorityApplicant priorityApplicant = priorityApplicantRepository.findPriorityApplicantById(priorityApplicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        ParkingSpace parkingSpace = parkingSpaceRepository.findParkingSpaceByDrawId(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));
        parkingSpace.decrementSlots();
        priorityApplicant.approveParkingSpaceToPriority(parkingSpace.getId(), ApprovalStatus.APPROVED);
        return PriorityApplicantConverter.toApprovePriorityResultDTO(parkingSpace);
    }
}
