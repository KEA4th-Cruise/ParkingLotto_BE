package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.*;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import static com.cruise.parkinglotto.web.converter.CertificateDocsConverter.toCertificateDocs;

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
    public void drawApply(ApplicantRequestDTO.ApplyDrawRequestDTO applyDrawRequestDTO, String accountId){
        Draw draw = drawRepository.findById(applyDrawRequestDTO.getDrawId()).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        if(draw.getStatus() != DrawStatus.OPEN){
            throw new ExceptionHandler(ErrorStatus.DRAW_NOT_IN_APPLY_PERIOD);
        }

        Optional<Member> memberOptional = memberRepository.findByAccountId(accountId);
        if (memberOptional.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        Member member = memberOptional.get();

        Optional<WeightDetails>weightDetailsOptional=weightDetailsRepository.findByMemberId(member.getId());

        //Handling carNum
        String carNum;
        if (applyDrawRequestDTO.getCarNum() != null) {
            carNum = applyDrawRequestDTO.getCarNum();
            memberRepository.updateCarNum(member.getId(), carNum);
        }
        else if (member.getCarNum() == null) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_CAR_NUM_NOT_FOUND); }

        //Handling CertFile
        if(!applyDrawRequestDTO.getGetCertFileUrlAndNameDTO().isEmpty()){
            List<CertificateDocsRequestDTO.CertifiCateFileDTO> certFileDTOList = applyDrawRequestDTO.getGetCertFileUrlAndNameDTO();
            List<CertificateDocs> certificateDocsList=toCertificateDocs(certFileDTOList, member);
            certificateDocsRepository.saveAll(certificateDocsList);
        }
        List<CertificateDocs> certificateDocsList = certificateDocsRepository.findByMemberId(member.getId());
        if (certificateDocsList.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_CERT_DOCUMENT_NOT_FOUND);
        }

        //Handling address
        String address;
        Integer trafficCommuteTime;
        Integer carCommuteTime;
        Double distance;
        if(applyDrawRequestDTO.getAddress() != null && applyDrawRequestDTO.getCarCommuteTime() != null && applyDrawRequestDTO.getTrafficCommuteTime() != null && applyDrawRequestDTO.getDistance()!=null ){
            address = applyDrawRequestDTO.getAddress();
            carCommuteTime = applyDrawRequestDTO.getCarCommuteTime();
            trafficCommuteTime = applyDrawRequestDTO.getTrafficCommuteTime();
            distance = applyDrawRequestDTO.getDistance();
            weightDetailsRepository.updateAddress(member, address);
        } else{
            if (weightDetailsOptional.isEmpty()) {
                throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_NOT_FOUND);
            }
            WeightDetails weightDetails=weightDetailsOptional.get();

            if(weightDetails.getAddress() != null && weightDetails.getCarCommuteTime() != null && weightDetails.getTrafficCommuteTime() != null && weightDetails.getDistance()!=null){
                carCommuteTime = weightDetails.getCarCommuteTime();
                trafficCommuteTime = weightDetails.getTrafficCommuteTime();
                distance = weightDetails.getDistance();
            }else{
                throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_ADDRESS_NOT_FOUND);
            }
        }

        //Handling workType
        WorkType workType;
        if(applyDrawRequestDTO.getWorkType() != null){
            workType = applyDrawRequestDTO.getWorkType();
        }else{
            if (weightDetailsOptional.isEmpty()) {
                throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_NOT_FOUND);
            }
            WeightDetails weightDetails=weightDetailsOptional.get();

            if(weightDetails.getWorkType() != null){
                workType = weightDetails.getWorkType();
            }else{
                throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_WORKTYPE_NOT_FOUND);
            }
        }

        //Handling userSeed when drawtype is general
        if(applyDrawRequestDTO.getUserSeed() == null && applyDrawRequestDTO.getDrawType()== DrawType.GENERAL){
            throw new ExceptionHandler(ErrorStatus.WEIGHTDETAILS_USER_SEED_NOT_FOUND);
        }
        String userSeed = applyDrawRequestDTO.getUserSeed();

        //recentLossCount
        Integer recentLossCount;
        if(weightDetailsOptional.isEmpty()){
            recentLossCount = 0;
        }else{
            WeightDetails weightDetails=weightDetailsOptional.get();
            recentLossCount = weightDetails.getRecentLossCount();
        }

        //Choice
        if(applyDrawRequestDTO.getFirstChoice() == null && applyDrawRequestDTO.getSecondChoice() == null && applyDrawRequestDTO.getDrawType() == DrawType.GENERAL){
            throw new ExceptionHandler(ErrorStatus.APPLICANT_WORK_TYPE_NOT_FOUND);
        }

        WinningStatus winningStatus = WinningStatus.PENDING;

        if(applyDrawRequestDTO.getDrawType()==DrawType.PRIORITY){
            Applicant applicant=ApplicantConverter.makeInitialPriorityApplicantObject(member,draw, winningStatus, distance, workType, trafficCommuteTime, carCommuteTime, recentLossCount);
            applicantRepository.save(applicant);
        }else{
            Applicant applicant=ApplicantConverter.makeInitialApplicantObject(member,draw, winningStatus, userSeed, applyDrawRequestDTO.getFirstChoice(), applyDrawRequestDTO.getSecondChoice(), distance, workType, trafficCommuteTime, carCommuteTime, recentLossCount);
            Applicant toGetApplicantId =applicantRepository.save(applicant);

            //userseedIndex 배정
            Integer maxUserSeedIndex = applicantRepository.findMaxUserSeedIndexByDraw(draw);
            Integer newUserSeedIndex = maxUserSeedIndex + 1;
            applicantRepository.updateUserSeedIndex(toGetApplicantId.getId(), newUserSeedIndex);

            //weight 계산 및 입력
            drawService.calculateWeight(applicant);
        }
    }
}
