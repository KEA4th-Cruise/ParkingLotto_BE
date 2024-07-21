package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.aws.AmazonConfig;
import com.cruise.parkinglotto.global.aws.AmazonS3Manager;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.MemberRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.converter.DrawConverter;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.cruise.parkinglotto.web.converter.DrawConverter.toGetCurrentDrawInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final ApplicantRepository applicantRepository;
    private final DrawRepository drawRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3Manager amazonS3Manager;
    private final AmazonConfig amazonConfig;


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
    @Transactional
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
        //순서 매기기
        List<Applicant> orderedApplicants = weightedRandomSelectionAll(applicants, new Random(seed.hashCode()));
        //당첨자 및 예비번호 부여
        handleDrawResults(drawId, orderedApplicants);
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public void handleDrawResults(Long drawId, List<Applicant> orderedApplicants) {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findByDrawId(drawId);
        long totalSlots = parkingSpaces.stream().mapToLong(ParkingSpace::getSlots).sum();

        //당첨자 리스트
        List<Applicant> selectedWinners = new ArrayList<>();
        //낙첨자 리스트
        List<Applicant> reserveApplicants = new ArrayList<>();

        for (int i = 0; i < orderedApplicants.size(); i++) {
            Applicant applicant = orderedApplicants.get(i);
            if (i < totalSlots) {
                // 당첨 처리
                selectedWinners.add(applicant);
                applicantRepository.updateReserveNum(applicant.getId(), 0);
                applicantRepository.updateWinningStatus(applicant.getId(), WinningStatus.WINNER);
                memberRepository.resetRecentLossCount(applicant.getMember().getId());
            } else {
                // 예비자 처리
                reserveApplicants.add(applicant);
                applicantRepository.updateWinningStatus(applicant.getId(), WinningStatus.RESERVE);
                memberRepository.increaseRecentLossCount(applicant.getMember().getId());
            }
        }

        // 당첨자들에게 자리 부여하기
        assignZones(drawId, selectedWinners);
        // 예비자들에게 예비번호 부여하기
        assignWaitListNumbers(reserveApplicants);
    }

    @Override
    @Transactional
    public void assignZones(Long drawId, List<Applicant> selectedWinners) {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findByDrawId(drawId);

        if (parkingSpaces == null || parkingSpaces.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND);
        }

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
    @Transactional
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
    @Transactional
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
    public static Applicant weightedRandomSelection(List<Applicant> applicants, Random random, Set<Long> selectedApplicantIds) {
        double totalWeight = applicants.stream()
                .filter(applicant -> !selectedApplicantIds.contains(applicant.getId()))
                .mapToDouble(Applicant::getWeightedTotalScore)
                .sum();
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
    public List<Applicant> weightedRandomSelectionAll(List<Applicant> applicants, Random random) {
        List<Applicant> orderedApplicants = new ArrayList<>();
        Set<Long> selectedApplicantIds = new HashSet<>();
        List<Applicant> availableApplicants = new ArrayList<>(applicants);

        while (!availableApplicants.isEmpty()) {
            Applicant applicant = weightedRandomSelection(availableApplicants, random, selectedApplicantIds);
            if (applicant != null) {
                orderedApplicants.add(applicant);
                selectedApplicantIds.add(applicant.getId());
                availableApplicants.remove(applicant);
            }
        }
        return orderedApplicants;
    }

    @Override
    @Transactional(readOnly = true)
    public DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo(HttpServletRequest httpServletRequest, Long drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        List<ParkingSpace> parkingSpace = parkingSpaceRepository.findByDrawId(draw.getId());

        if (parkingSpace == null || parkingSpace.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND);
        }

        return toGetCurrentDrawInfo(draw, parkingSpace);
    }

    @Override
    @Transactional
    public Draw createDraw(MultipartFile mapImage, DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startAt = createDrawRequestDTO.getUsageStartAt().format(formatter);
        String year = startAt.substring(0, 4);
        String quarter = String.valueOf((Long.parseLong(startAt.substring(5, 7)) - 1) / 3 + 1);
        String drawType = (createDrawRequestDTO.getType() == DrawType.GENERAL) ? "일반추첨" : "우대신청";
        String title = year + "년도 " + quarter + "분기 " + drawType;
        String mapImageUrl = amazonS3Manager.uploadFileToDirectory(amazonConfig.getMapImagePath(), title.replace(" ", "_"), mapImage);
        Draw draw = DrawConverter.toDraw(createDrawRequestDTO, title, mapImageUrl, year, quarter);
        return drawRepository.save(draw);
    }

    @Override
    @Transactional
    public DrawResponseDTO.ConfirmDrawCreationResultDTO confirmDrawCreation(Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        List<ParkingSpace> parkingSpaceList = parkingSpaceRepository.findByDrawId(drawId);
        if (parkingSpaceList == null || parkingSpaceList.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND);
        }
        parkingSpaceList.forEach(parkingSpace -> parkingSpace.updateConfirmed(true));
        Long totalSlots = parkingSpaceList.stream()
                .mapToLong(ParkingSpace::getSlots)
                .sum();
        draw.updateConfirmed(true, totalSlots);

        return DrawConverter.toConfirmDrawCreationResultDTO(draw, parkingSpaceList);
    }
}
