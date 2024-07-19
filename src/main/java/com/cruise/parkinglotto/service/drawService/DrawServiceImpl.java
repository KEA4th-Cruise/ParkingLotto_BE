package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.MemberRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.cruise.parkinglotto.web.converter.DrawConverter.toGetCurrentDrawInfo;

@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final ApplicantRepository applicantRepository;
    private final DrawRepository drawRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final MemberRepository memberRepository;

    //계산용 변수
    private static final int WORK_TYPE1_SCORE = 25;
    private static final int WORK_TYPE2_SCORE = 5;
    private static final int TRAFFIC_COMMUTE_MAX_SCORE = 30;
    private static final int TRAFFIC_COMMUTE_BASE_SCORE = 10;
    private static final int CAR_COMMUTE_MAX_SCORE = 5;
    private static final int COMMUTE_DIFF_MAX_SCORE = 5;
    private static final int DISTANCE_MAX_SCORE = 20;
    private static final int RECENT_LOSS_COUNT_BASE_SCORE = 10;
    private static final int RECENT_LOSS_COUNT_EXTRA_SCORE = 5;

    @Override
    public void executeDraw(Long drawId) {

        //해당 회차 시드 생성
        updateSeedNum(drawId);
        //시드 번호 가져오기
        String seed = drawRepository.findById(drawId).orElseThrow(() ->
                        new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND))
                .getSeedNum();
        //신청자들에게 난수 부여
        assignRandomNumber(drawId, seed);
        //신청자 목록 가져오기
        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
        //가중치 계산하기
        for (Applicant applicant : applicants) {
            calculateWeight(applicant);
        }
        //당첨자 뽑기
        List<Applicant> selectedWinners = selectWinners(drawId, applicants, new Random(seed.hashCode()));
        //당첨자들에게 자리 부여하기
        assignZones(drawId, selectedWinners);
        //낙첨자들에게 예비번호 부여하기
        assignWaitListNumbers(applicants);
    }

    @Override
    public void updateSeedNum(Long drawId) {
        try {
            //추첨에 대한 예외처리
            drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

            List<Applicant> applicants = applicantRepository.findByDrawId(drawId);

            if (applicants == null || applicants.isEmpty()) {
                throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
            }
            String seed = applicants.stream()
                    .map(Applicant::getUserSeed)
                    .collect(Collectors.joining());
            drawRepository.updateSeedNum(drawId, seed);
        } catch (IllegalArgumentException e) {
            System.err.println("Error : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred while retrieving applicants for draw ID: " + drawId);
        }
    }

    @Override
    public void assignRandomNumber(Long drawId, String seed) {
        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
        if (applicants == null || applicants.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }
        //해당 회차 시드로 생성된 첫 난수를 맨 처음 멤버에게 부여
        Random random = new Random(seed.hashCode());
        double randomNumber = random.nextDouble();
        applicantRepository.assignRandomNumber(applicants.get(0).getId(), randomNumber);
        //해당 난수를 시드로 하여 모든 신청자들에게 난수 부여
        for (int i = 1; i < applicants.size(); i++) {
            random = new Random(Double.doubleToLongBits(randomNumber));
            randomNumber = random.nextDouble();
            applicantRepository.assignRandomNumber(applicants.get(i).getId(), randomNumber);
        }
    }

    @Override
    public List<Applicant> selectWinners(Long drawId, List<Applicant> applicants, Random random) {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findByDrawId(drawId);
        Long totalSlots = parkingSpaces.stream().mapToLong(ParkingSpace::getSlots).sum();

        //난수를 기준으로 응모자를 정렬
        applicants.sort(Comparator.comparingDouble(Applicant::getRandomNumber));

        List<Applicant> selectedWinners = new ArrayList<>();
        for (int i = 0; i < totalSlots && !applicants.isEmpty(); i++) {
            Applicant winner = weightedRandomSelection(applicants, random);
            if (winner != null) {
                /**
                 * 1. 예비번호를 0으로 바꿔서 당첨 표시
                 * 2. Member테이블에서 해당 사용자의 연속낙첨횟수 0으로 바꾸기
                 */
                selectedWinners.add(winner);
                applicantRepository.updateReserveNum(winner.getMember().getId(), 0);
                memberRepository.resetRecentLossCount(winner.getMember().getId());
            }
        }
        return selectedWinners;
    }


    @Override
    public void assignZones(Long drawId, List<Applicant> selectedWinners) {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findByDrawId(drawId);
        Map<Long, Long> zoneCapacityMap = parkingSpaces.stream()
                .collect(Collectors.toMap(ParkingSpace::getId, ParkingSpace::getSlots));

        Map<Long, List<Applicant>> zoneAssignments = new HashMap<>();

        for (Long zone : zoneCapacityMap.keySet()) {
            zoneAssignments.put(zone, new ArrayList<>());
        }

        for (Applicant winner : selectedWinners) {
            Long assignedZone = null;
            if (zoneAssignments.get(winner.getFirstChoice()).size() < zoneCapacityMap.get(winner.getFirstChoice())) {
                zoneAssignments.get(winner.getFirstChoice()).add(winner);
                assignedZone = winner.getFirstChoice();
            } else if (zoneAssignments.get(winner.getSecondChoice()).size() < zoneCapacityMap.get(winner.getSecondChoice())) {
                zoneAssignments.get(winner.getSecondChoice()).add(winner);
                assignedZone = winner.getSecondChoice();
            } else {
                for (Long zone : zoneCapacityMap.keySet()) {
                    if (zoneAssignments.get(zone).size() < zoneCapacityMap.get(zone)) {
                        zoneAssignments.get(zone).add(winner);
                        assignedZone = zone;
                        break;
                    }
                }
            }
            //당첨되면 RemainSlots 1 줄이는 로직
            if (assignedZone != null) {
                applicantRepository.updateParkingSpaceId(winner.getId(), assignedZone);
                parkingSpaceRepository.decrementRemainSlots(assignedZone);
            }
        }
    }

    @Override
    public void calculateWeight(Applicant applicant) {
        Member member = applicant.getMember();
        double weight = 0;

        // 근무타입에 따른 점수 부여
        if (WorkType.TYPE1.equals(member.getWorkType())) {
            weight += WORK_TYPE1_SCORE;
        } else if (WorkType.TYPE2.equals(member.getWorkType())) {
            weight += WORK_TYPE2_SCORE;
        }

        // 대중교통 통근시간에 따른 점수 부여
        long trafficCommuteTime = applicant.getTrafficCommuteTime();
        if (trafficCommuteTime < 60) {
            weight += TRAFFIC_COMMUTE_BASE_SCORE + 9 * (1 - Math.exp(-0.2 * trafficCommuteTime));
        } else {
            weight += TRAFFIC_COMMUTE_BASE_SCORE + (TRAFFIC_COMMUTE_MAX_SCORE - TRAFFIC_COMMUTE_BASE_SCORE)
                    * (1 - Math.exp(-0.05 * (trafficCommuteTime - 60)));
        }

        // 자가용 통근시간에 따른 점수 부여
        long carCommuteTime = applicant.getCarCommuteTime();
        weight += CAR_COMMUTE_MAX_SCORE * (1 - Math.exp(-0.05 * carCommuteTime));

        // 대중교통시간 - 자가용 통근시간 차이에 따른 점수 부여
        long commuteTimeDiff = Math.abs(trafficCommuteTime - carCommuteTime);
        weight += COMMUTE_DIFF_MAX_SCORE * (1 - Math.exp(-0.05 * commuteTimeDiff));

        // 직선거리에 따른 점수 부여
        double distance = applicant.getDistance();
        weight += DISTANCE_MAX_SCORE * (1 - Math.exp(-0.02 * distance));

        // 연속낙첨횟수에 따른 점수 부여
        long recentLossCount = member.getRecentLossCount();
        if (recentLossCount < 4) {
            weight += RECENT_LOSS_COUNT_BASE_SCORE * (1 - Math.exp(-0.3 * recentLossCount));
        } else {
            weight += RECENT_LOSS_COUNT_BASE_SCORE + RECENT_LOSS_COUNT_EXTRA_SCORE
                    * (1 - Math.exp(-0.7 * (recentLossCount - 3)));
        }
        // 가중치 점수를 Applicant 객체에 설정
        applicantRepository.updateWeightedTotalScore(applicant.getId(), weight);
        }

    //예비번호 부여 로직 및 예비번호를 받는 즉시 연속 낙첨 횟수 증가
    @Override
    public void assignWaitListNumbers(List<Applicant> applicants) {
        int waitListNumber = 1;
        for (Applicant applicant : applicants) {
            if (applicant.getReserveNum() != 0) {
                applicantRepository.updateReserveNum(applicant.getId(), waitListNumber++);
                memberRepository.increaseRecentLossCount(applicant.getMember().getId());
            }
        }
    }

    //가중치랜덤알고리즘 로직
    public static Applicant weightedRandomSelection(List<Applicant> applicants, Random random) {
        double totalWeight = applicants.stream().mapToDouble(Applicant::getWeightedTotalScore).sum();
        if (totalWeight <= 0) {
            return null;
        }
        double randomIndex = random.nextDouble(totalWeight);
        int currentWeightSum = 0;

        for (Applicant applicant : applicants) {
            currentWeightSum += applicant.getWeightedTotalScore();
            if (randomIndex < currentWeightSum) {
                return applicant;
            }
        }
        return null; // 만약 적합한 응모자가 없을 경우 null 반환
    }

    @Override
    @Transactional(readOnly = true)
    public DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo(HttpServletRequest httpServletRequest, DrawRequestDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo) {
        Optional<Draw> drawOptional = drawRepository.findById(getCurrentDrawInfo.getDrawId());
        Draw draw = drawOptional.orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        Optional<ParkingSpace> parkingSpaceOptional = parkingSpaceRepository.findByIdAndDrawId(getCurrentDrawInfo.getParkingId(), draw.getId());
        ParkingSpace parkingSpace = parkingSpaceOptional.orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));

        return toGetCurrentDrawInfo(draw, parkingSpace);
    }
}
