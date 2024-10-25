package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.*;
import com.cruise.parkinglotto.global.excel.FileGeneration;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;
import com.cruise.parkinglotto.global.mail.MailType;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.*;
import com.cruise.parkinglotto.service.drawStatisticsService.DrawStatisticsService;
import com.cruise.parkinglotto.service.mailService.MailService;
import com.cruise.parkinglotto.service.weightSectionStatisticsService.WeightSectionStatisticsService;
import com.cruise.parkinglotto.web.converter.DrawConverter;
import com.cruise.parkinglotto.web.converter.MailInfoConverter;
import com.cruise.parkinglotto.web.converter.ParkingSpaceConverter;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.SimulationData;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cruise.parkinglotto.web.converter.DrawConverter.toDrawResultExcelDTO;
import static com.cruise.parkinglotto.web.converter.DrawConverter.toGetCurrentDrawInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final ApplicantRepository applicantRepository;
    private final DrawRepository drawRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final WeightDetailsRepository weightDetailsRepository;
    private final ObjectStorageService objectStorageService;
    private final ObjectStorageConfig objectStorageConfig;
    private final JwtUtils jwtUtils;
    private final MemberRepository memberRepository;
    private final FileGeneration fileGenerationService;
    private final WeightSectionStatisticsService weightSectionStatisticsService;
    private final DrawStatisticsService drawStatisticsService;
    private final @Lazy TaskScheduler taskScheduler;
    private final PriorityApplicantRepository priorityApplicantRepository;
    private final MailService mailService;
    private final CertificateDocsRepository certificateDocsRepository;

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
    public void executeDraw(Long drawId) throws IOException, MessagingException, NoSuchAlgorithmException {
        try {
            Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

            if (draw.getStatus() == DrawStatus.COMPLETED) {
                throw new ExceptionHandler(ErrorStatus.DRAW_ALREADY_EXECUTED);
            }
            if (draw.getStatus() != DrawStatus.CLOSED) {
                throw new ExceptionHandler(ErrorStatus.DRAW_NOT_READY);
            }
            updateSeedNum(drawId);
            String seed = draw.getSeedNum();
            if (seed == null) {
                throw new ExceptionHandler(ErrorStatus.DRAW_SEED_NOT_FOUND);
            }
            assignRandomNumber(drawId, seed);
            List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
            for (Applicant applicant : applicants) {
                calculateWeight(applicant);
            }
            List<Applicant> orderedApplicants = weightedRandomSelectionAll(applicants, new Random(seed.hashCode()));

            handleDrawResults(drawId, orderedApplicants);

            draw.updateStatus(DrawStatus.COMPLETED);

            weightSectionStatisticsService.updateWeightSectionStatistics(drawId);

            drawStatisticsService.updateDrawStatistics(drawId, orderedApplicants);

            String url = fileGenerationService.generateAndUploadExcel(draw, orderedApplicants);

            draw.updateResultURL(url);

            drawRepository.save(draw);

            for (Applicant orderedApplicant : orderedApplicants) {
                if (orderedApplicant.getWinningStatus() == WinningStatus.RESERVE) {
                    orderedApplicant.updateParkingSpaceId(-1L);
                }
            }

        } catch (Exception e) {
            log.error("Error occurred during executeDraw for drawId: {}", drawId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateSeedNum(Long drawId) {
        //추첨에 대한 예외처리
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);

        if (applicants == null || applicants.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }
        String seed = applicants.stream()
                .map(Applicant::getUserSeed)
                .collect(Collectors.joining());
        draw.updateSeedNum(seed);
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
        applicants.get(0).assignRandomNumber(randomNumber);
        //해당 난수를 시드로 하여 모든 신청자들에게 난수 부여
        for (int i = 1; i < applicants.size(); i++) {
            random = new Random(Double.doubleToLongBits(randomNumber));
            randomNumber = random.nextDouble();
            applicants.get(i).assignRandomNumber(randomNumber);
        }
    }

    @Override
    @Transactional
    public void handleDrawResults(Long drawId, List<Applicant> orderedApplicants) throws MessagingException, NoSuchAlgorithmException {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findByDrawId(drawId);
        int totalSlots = parkingSpaces.stream().mapToInt(ParkingSpace::getSlots).sum();
        //당첨자 리스트
        List<Applicant> selectedWinners = new ArrayList<>();
        //낙첨자 리스트
        List<Applicant> reserveApplicants = new ArrayList<>();

        for (int i = 0; i < orderedApplicants.size(); i++) {
            Applicant applicant = orderedApplicants.get(i);
            if (i < totalSlots) {
                // 당첨 처리
                selectedWinners.add(applicant);
                mailService.sendEmailForCertification(MailInfoConverter.toMailInfo(applicant.getMember().getEmail(), applicant.getMember().getNameKo(), MailType.WINNER));
                applicant.updateReserveNum(0);
                applicant.updateWinningStatus(WinningStatus.WINNER);
                weightDetailsRepository.resetRecentLossCount(applicant.getMember());
            } else {
                // 예비자 처리
                reserveApplicants.add(applicant);
                mailService.sendEmailForCertification(MailInfoConverter.toMailInfo(applicant.getMember().getEmail(), applicant.getMember().getNameKo(), MailType.RESERVE));
                applicant.updateWinningStatus(WinningStatus.RESERVE);
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

        Map<Long, Integer> zoneCapacityMap = parkingSpaces.stream()
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
        double weight = 0.0;

        // 근무타입에 따른 점수 부여
        if (WorkType.TYPE1.equals(applicant.getWorkType())) {
            weight += WORK_TYPE1_SCORE;
        } else if (WorkType.TYPE2.equals(applicant.getWorkType())) {
            weight += WORK_TYPE2_SCORE;
        }

        // 대중교통 통근시간에 따른 점수 부여
        Integer trafficCommuteTime = applicant.getTrafficCommuteTime();
        if (trafficCommuteTime < 60) {
            weight += TRAFFIC_COMMUTE_BASE_SCORE + 9 * (1 - Math.exp(-0.2 * trafficCommuteTime));
        } else {
            weight += TRAFFIC_COMMUTE_BASE_SCORE + (TRAFFIC_COMMUTE_MAX_SCORE - TRAFFIC_COMMUTE_BASE_SCORE)
                    * (1 - Math.exp(-0.05 * (trafficCommuteTime - 60)));
        }

        // 자가용 통근시간에 따른 점수 부여
        Integer carCommuteTime = applicant.getCarCommuteTime();
        weight += CAR_COMMUTE_MAX_SCORE * (1 - Math.exp(-0.05 * carCommuteTime));

        // 대중교통시간 - 자가용 통근시간 차이에 따른 점수 부여
        int commuteTimeDiff = Math.abs(trafficCommuteTime - carCommuteTime);
        weight += COMMUTE_DIFF_MAX_SCORE * (1 - Math.exp(-0.05 * commuteTimeDiff));

        // 직선거리에 따른 점수 부여
        Double distance = applicant.getDistance();
        weight += DISTANCE_MAX_SCORE * (1 - Math.exp(-0.02 * distance));

        // 연속낙첨횟수에 따른 점수 부여
        Integer recentLossCount = applicant.getRecentLossCount();
        if (recentLossCount < 4) {
            weight += RECENT_LOSS_COUNT_BASE_SCORE * (1 - Math.exp(-0.3 * recentLossCount));
        } else {
            weight += RECENT_LOSS_COUNT_BASE_SCORE + RECENT_LOSS_COUNT_EXTRA_SCORE
                    * (1 - Math.exp(-0.7 * (recentLossCount - 3)));
        }
        // 가중치 점수를 Applicant 객체에 설정
        applicant.updateTotalWeightedScore(weight);
    }

    //예비번호 부여 로직 및 예비번호를 받는 즉시 연속 낙첨 횟수 증가
    @Override
    @Transactional
    public void assignWaitListNumbers(List<Applicant> applicants) {
        int waitListNumber = 1;
        for (Applicant applicant : applicants) {
            if (applicant.getReserveNum() != 0) {
                applicant.updateReserveNum(waitListNumber++);
                weightDetailsRepository.increaseRecentLossCount(applicant.getMember());
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
    public Page<Applicant> getDrawResult(HttpServletRequest httpServletRequest, Long drawId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 15);

        drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Page<Applicant> applicantsPage = applicantRepository.findByDrawId(pageRequest, drawId);

        if (applicantsPage.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }
        return applicantsPage;
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
        List<String> imageTypeList = Arrays.asList("image/png", "image/jpeg", "image/jpg");
        String mapImageMimeType = mapImage.getContentType();
        if (mapImageMimeType == null || !imageTypeList.contains(mapImageMimeType)) {
            throw new ExceptionHandler(ErrorStatus.FILE_FORMAT_NOT_SUPPORTED);
        }
        if (createDrawRequestDTO.getDrawStartAt().isBefore(LocalDateTime.now())) {
            throw new ExceptionHandler(ErrorStatus.INVALID_DRAW_START_DATE);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startAt = createDrawRequestDTO.getUsageStartAt().format(formatter);
        String year = startAt.substring(0, 4);
        String quarter = String.valueOf((Long.parseLong(startAt.substring(5, 7)) - 1) / 3 + 1);
        String drawType = (createDrawRequestDTO.getType() == DrawType.GENERAL) ? "일반추첨" : "우대신청";
        String title = year + "년도 " + quarter + "분기 " + drawType;
        String mapImageUrl = objectStorageService.uploadObject(objectStorageConfig.getMapImagePath(), title.replace(" ", "_"), mapImage);
        Draw draw = DrawConverter.toDraw(createDrawRequestDTO, title, mapImageUrl, year, quarter);
        try {
            return drawRepository.save(draw);
        } catch (DataIntegrityViolationException ignored) {
            throw new ExceptionHandler(ErrorStatus.DRAW_ALREADY_EXIST);
        }
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
        Integer totalSlots = parkingSpaceList.stream()
                .mapToInt(ParkingSpace::getSlots)
                .sum();
        draw.updateConfirmed(true, totalSlots);
        deleteUnconfirmedDrawsAndParkingSpaces();
        openDraw(draw);
        closeDraw(draw);
        return DrawConverter.toConfirmDrawCreationResultDTO(draw, parkingSpaceList);
    }

    @Override
    public DrawResponseDTO.GetDrawOverviewResultDTO getDrawOverview(HttpServletRequest httpServletRequest) {
        String loginMemberAccountId = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member loginMember = memberRepository.findByAccountId(loginMemberAccountId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        //  상태가 PENDING이 아니고, 사용 날짜 기준 가장 최신 추첨
        Optional<Draw> latestDraw = drawRepository.findTopByStatusNotOrderByUsageStartAtDesc(DrawStatus.PENDING);
        Boolean isApplied;  //  이 값은 우대신청 or 일반추첨 상관 없이 신청기간일 때만 의미 있는 값임. 신청 기간인 경우 이 플래그를 통해 신청 수정하기 or 취소하기를 띄워줘여함
        Integer applicantsCount;
        List<ParkingSpace> parkingSpaceList;
        List<ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO> parkingSpaceCompetitionRateDTOList;
        if (latestDraw.isEmpty()) {  //  PENDING이 아닌 추첨이 없을 경우 (최초의 상태: 진행된 추첨이 아직 존재하지 않음)
            throw new ExceptionHandler(ErrorStatus.DRAW_STATISTICS_NOT_EXIST);
        } else {
            if (latestDraw.get().getType() == DrawType.PRIORITY) {   // 가장 최근 추첨이 우대 신청일 경우
                Optional<PriorityApplicant> priorityApplicant = priorityApplicantRepository.findByDrawIdAndMemberId(latestDraw.get().getId(), loginMember.getId());
                isApplied = priorityApplicant.isPresent();
                return DrawConverter.toGetDrawOverviewResultDTO(isApplied, null, latestDraw.get(), null);
            } else {    //  일반 추첨일 경우
                Optional<Applicant> applicant = applicantRepository.findByDrawIdAndMemberId(latestDraw.get().getId(), loginMember.getId());
                isApplied = applicant.isPresent();
                parkingSpaceList = parkingSpaceRepository.findByDrawId(latestDraw.get().getId());
                if (latestDraw.get().getStatus() == DrawStatus.OPEN) {  //   신청 기간인 경우 신청자 테이블에서 구역별 신청자 수를 계산한다.
                    applicantsCount = applicantRepository.countByDrawId(latestDraw.get().getId());
                    parkingSpaceCompetitionRateDTOList = parkingSpaceList.stream()
                            .map(parkingSpace -> {
                                Integer applicantsCountPerParkingSpace = applicantRepository.countByDrawIdAndFirstChoice(latestDraw.get().getId(), parkingSpace.getId());
                                return ParkingSpaceConverter.toParkingSpaceCompetitionRateDTO(parkingSpace, applicantsCountPerParkingSpace);
                            }).toList();
                } else { //  CLOSED or COMPLETED 일 경우 주차구역 테이블에서 총 신청자 수를 확인한다.
                    applicantsCount = latestDraw.get().getDrawStatistics().getTotalApplicants();
                    parkingSpaceCompetitionRateDTOList = parkingSpaceList.stream()
                            .map(parkingSpace -> ParkingSpaceConverter.toParkingSpaceCompetitionRateDTO(parkingSpace, parkingSpace.getApplicantCount())).toList();
                }
                return DrawConverter.toGetDrawOverviewResultDTO(isApplied, applicantsCount, latestDraw.get(), parkingSpaceCompetitionRateDTOList);
            }
        }
    }

    @Async
    @Override
    public void deleteUnconfirmedDrawsAndParkingSpaces() {
        List<ParkingSpace> unconfirmedParkingSpaceList = parkingSpaceRepository.findByConfirmed(false);
        unconfirmedParkingSpaceList.forEach(parkingSpace -> {
            objectStorageService.deleteObject(parkingSpace.getFloorPlanImageUrl());
        });
        parkingSpaceRepository.deleteAll(unconfirmedParkingSpaceList);

        List<Draw> unconfirmedDrawList = drawRepository.findByConfirmed(false);
        unconfirmedDrawList.forEach(draw -> {
            objectStorageService.deleteObject(draw.getMapImageUrl());

        });
        drawRepository.deleteAll(unconfirmedDrawList);
    }

    @Override
    @Transactional(readOnly = true)
    public DrawResponseDTO.SimulateDrawResponseDTO simulateDraw(Long drawId, String seed, Integer page) {

        int pageSize = 15;
        int offset = (page - 1) * pageSize;

        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
        if (applicants == null || applicants.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }

        // 시뮬레이션 데이터를 저장할 HashMap 생성
        Map<Long, SimulationData> simulationDataMap = new HashMap<>();

        // 가중치 정보 가져오기
        for (Applicant applicant : applicants) {
            simulationDataMap.put(applicant.getId(), new SimulationData(0.0, applicant.getWeightedTotalScore(), -1L, -1, WinningStatus.PENDING));
        }

        // 주어진 시드로 난수 부여하기
        // 주어진 시드로 첫 번째 신청자에게 난수 부여
        Random random = new Random(seed.hashCode());
        double randomNumber = random.nextDouble();
        simulationDataMap.get(applicants.get(0).getId()).setRandomNumber(randomNumber);

        // 첫 번째 난수를 시드로 하여 나머지 신청자들에게 난수 부여
        for (int i = 1; i < applicants.size(); i++) {
            random = new Random(Double.doubleToLongBits(randomNumber));
            randomNumber = random.nextDouble();
            simulationDataMap.get(applicants.get(i).getId()).setRandomNumber(randomNumber);
        }

        // 가중치 랜덤 선택 메서드
        List<Applicant> orderedApplicants = weightedRandomSelectionAll(new ArrayList<>(applicants), new Random(seed.hashCode()));

        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findByDrawId(drawId);
        int totalSlots = parkingSpaces.stream().mapToInt(ParkingSpace::getSlots).sum();

        List<Applicant> selectedWinners = new ArrayList<>();
        List<Applicant> reserveApplicants = new ArrayList<>();
        for (int i = 0; i < orderedApplicants.size(); i++) {
            Applicant applicant = orderedApplicants.get(i);
            SimulationData simulationData = simulationDataMap.get(applicant.getId());
            if (i < totalSlots) {
                selectedWinners.add(applicant);
                simulationData.setWinningStatus(WinningStatus.WINNER);
            } else {
                reserveApplicants.add(applicant);
                simulationData.setWinningStatus(WinningStatus.RESERVE);
            }
        }
        // 주차 공간 할당 로직
        Map<Long, Integer> zoneCapacityMap = parkingSpaces.stream()
                .collect(Collectors.toMap(ParkingSpace::getId, ParkingSpace::getSlots));

        Map<Long, List<Applicant>> zoneAssignments = new HashMap<>();
        for (Long zone : zoneCapacityMap.keySet()) {
            zoneAssignments.put(zone, new ArrayList<>());
        }

        for (Applicant winner : selectedWinners) {
            Long assignedZone = null;

            // 첫 번째 선택지에 자리 확인 및 할당
            if (zoneCapacityMap.get(winner.getFirstChoice()) > 0) {
                zoneAssignments.get(winner.getFirstChoice()).add(winner);
                assignedZone = winner.getFirstChoice();
            }
            // 첫 번째 선택지에 자리가 없으면 두 번째 선택지 확인 및 할당
            else if (zoneCapacityMap.get(winner.getSecondChoice()) > 0) {
                zoneAssignments.get(winner.getSecondChoice()).add(winner);
                assignedZone = winner.getSecondChoice();
            }
            // 두 번째 선택지에 자리가 없으면 나머지 구역에서 자리 확인 및 할당
            else {
                for (Long zone : zoneCapacityMap.keySet()) {
                    if (zoneCapacityMap.get(zone) > 0) {
                        zoneAssignments.get(zone).add(winner);
                        assignedZone = zone;
                        break;
                    }
                }
            }

            if (assignedZone != null) {
                SimulationData simData = simulationDataMap.get(winner.getId());
                simData.setParkingSpaceId(assignedZone);
                zoneCapacityMap.put(assignedZone, zoneCapacityMap.get(assignedZone) - 1);
            } else {
                log.error("No available parking space for winner = " + winner.getId());
            }
        }
        // 예비자에게 예비번호 부여하기
        int waitListNumber = 1;
        for (Applicant applicant : reserveApplicants) {
            SimulationData simData = simulationDataMap.get(applicant.getId());
            simData.setReserveNum(waitListNumber++);
        }

        // 모든 응모자를 포함하는 DTO 리스트 생성
        List<DrawResponseDTO.SimulateApplicantDTO> allApplicantsDTO = new ArrayList<>();
        for (Applicant applicant : selectedWinners) {
            SimulationData simData = simulationDataMap.get(applicant.getId());
            allApplicantsDTO.add(DrawConverter.toSimulateApplicantDTO(applicant, simData));
        }
        for (Applicant applicant : reserveApplicants) {
            SimulationData simData = simulationDataMap.get(applicant.getId());
            allApplicantsDTO.add(DrawConverter.toSimulateApplicantDTO(applicant, simData));
        }

        // 페이징 처리
        int start = Math.min(offset, allApplicantsDTO.size());
        int end = Math.min((offset + pageSize), allApplicantsDTO.size());

        List<DrawResponseDTO.SimulateApplicantDTO> pagedApplicants = allApplicantsDTO.subList(start, end);
        return DrawConverter.toSimulateDrawResponseDTO(drawId, seed, simulationDataMap, pagedApplicants, allApplicantsDTO.size());
    }

    @Override
    public DrawResponseDTO.DrawResultExcelDTO getDrawResultExcel(Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        return toDrawResultExcelDTO(draw.getResultURL());
    }

    @Override
    public DrawResponseDTO.GetDrawInfoDetailDTO getDrawInfoDetail(HttpServletRequest httpServletRequest, Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        List<Applicant> applicants = applicantRepository.findByDrawId(drawId);
        if (applicants == null || applicants.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND);
        }
        return DrawConverter.toGetDrawInfoDetail(draw, applicants);
    }

    @Override
    @Transactional(readOnly = true)
    public DrawResponseDTO.GetDrawListResultDTO getDrawList(String year, DrawType drawType) {
        List<Draw> drawList = drawRepository.findByYearAndTypeAndConfirmedTrueOrderByUsageStartAtDesc(year, drawType);
        List<String> yearList = drawRepository.findYearList();
        return DrawConverter.toGetDrawListResultDTO(yearList, drawList);
    }

    @Transactional
    public void assignReservedApplicant(Long drawId, Long winnerId) throws MessagingException, NoSuchAlgorithmException {
        Applicant currentWinner = applicantRepository.findByDrawIdAndId(drawId, winnerId);

        Long parkingSpaceId = currentWinner.getParkingSpaceId();
        Applicant nextWinner = applicantRepository.findByDrawIdAndReserveNum(drawId, 1);

        nextWinner.updateReserve(parkingSpaceId, 0, WinningStatus.WINNER);

        mailService.sendEmailForCertification(MailInfoConverter.toMailInfo(nextWinner.getMember().getEmail(), nextWinner.getMember().getNameKo(), MailType.RESERVE_WINNER));

        List<Applicant> reservedApplicants = applicantRepository.findByDrawIdAndReserveNumGreaterThan(drawId, 1);

        for (Applicant applicant : reservedApplicants) {
            applicant.updateReserveNum(applicant.getReserveNum() - 1);
        }
        currentWinner.updateReserve(-1L, reservedApplicants.size() + 1, WinningStatus.CANCELED);
    }

    @Override
    @Transactional(readOnly = true)
    public DrawResponseDTO.GetYearsFromDrawListDTO getYearsFromDrawList() {
        List<String> yearList = drawRepository.findYearList();
        return DrawConverter.toGetYearsFromDrawListDTO(yearList);
    }

    @Transactional
    public void adminCancelWinner(HttpServletRequest httpServletRequest, Long drawId, Long winnerId) throws MessagingException, NoSuchAlgorithmException {
        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member member = memberRepository.findByAccountId(accountIdFromRequest).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Applicant cancelApplicant = applicantRepository.findByDrawIdAndId(drawId, winnerId);
        if (member.getAccountType() == AccountType.ADMIN) {
            if (cancelApplicant.getWinningStatus() == WinningStatus.WINNER) {
                assignReservedApplicant(drawId, winnerId);
            } else {
                throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_WINNING_STATUS);
            }
        } else {
            throw new ExceptionHandler(ErrorStatus._UNAUTHORIZED_ACCESS);
        }
    }

    @Transactional
    public void selfCancelWinner(HttpServletRequest httpServletRequest, Long drawId) throws MessagingException, NoSuchAlgorithmException {
        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member member = memberRepository.findByAccountId(accountIdFromRequest).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Applicant winner = applicantRepository.findByDrawIdAndMemberId(drawId, member.getId()).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        if (winner.getWinningStatus() == WinningStatus.WINNER) {
            assignReservedApplicant(drawId, winner.getId());
        } else {
            throw new ExceptionHandler(ErrorStatus.APPLICANT_NOT_WINNING_STATUS);
        }

    }

    @Override
    public void openDraw(Draw draw) {
        Runnable task = () -> {
            log.info("Opening draw with ID: {}", draw.getId());
            draw.updateStatus(DrawStatus.OPEN);
            drawRepository.save(draw);
        };
        LocalDateTime drawEndTime = draw.getDrawStartAt();
        Date startTime = Date.from(drawEndTime.atZone(ZoneId.systemDefault()).toInstant());
        taskScheduler.schedule(task, startTime);
    }

    @Override
    public void closeDraw(Draw draw) {
        Runnable task = () -> {
            log.info("Closing draw with ID: {}", draw.getId());
            draw.updateStatus(DrawStatus.CLOSED);
            drawRepository.save(draw);
        };
        LocalDateTime drawEndTime = draw.getDrawEndAt();
        Date endTime = Date.from(drawEndTime.atZone(ZoneId.systemDefault()).toInstant());
        taskScheduler.schedule(task, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DrawResponseDTO.GetAppliedDrawResultDTO> getAppliedDrawList(String accountId, Integer page) {

        Member member = memberRepository.findByAccountId(accountId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Long memberId = member.getId();

        List<DrawResponseDTO.GetAppliedDrawResultDTO> generalDrawList = applicantRepository.findByMemberId(memberId)
                .stream()
                .map(applicant -> {
                    Long drawStatisticsId;
                    if (!applicant.getDraw().getStatus().equals(DrawStatus.COMPLETED)) {
                        drawStatisticsId = null;
                    } else drawStatisticsId = applicant.getDraw().getDrawStatistics().getId();
                    return DrawConverter.toGetAppliedDrawResultDTO(applicant.getDraw().getId(), applicant.getReserveNum(), applicant.getDraw().getTitle(), applicant.getDraw().getType(), drawStatisticsId, applicant.getParkingSpaceId(), applicant.getDraw().getUsageStartAt(), applicant.getWinningStatus());
                })
                .toList();
        List<DrawResponseDTO.GetAppliedDrawResultDTO> priorityDrawList = priorityApplicantRepository.findByMemberId(memberId)
                .stream()
                .map(priorityApplicant -> DrawConverter.toGetAppliedDrawResultDTO(priorityApplicant.getDraw().getId(), -1, priorityApplicant.getDraw().getTitle(), priorityApplicant.getDraw().getType(), null, priorityApplicant.getParkingSpaceId(), priorityApplicant.getDraw().getUsageStartAt(), null))
                .toList();
        List<DrawResponseDTO.GetAppliedDrawResultDTO> appliedDrawList = Stream.concat(generalDrawList.stream(), priorityDrawList.stream())
                .distinct()
                .sorted((general, priority) -> priority.getUsageStartAt().compareTo(general.getUsageStartAt()))
                .toList();

        PageRequest pageRequest = PageRequest.of(page, 4);
        int total = appliedDrawList.size();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), total);

        List<DrawResponseDTO.GetAppliedDrawResultDTO> subList = appliedDrawList.subList(start, end);
        return new PageImpl<>(subList, pageRequest, total);
    }

    @Override
    @Transactional
    public void deleteDraw(Long drawId, String accountId) {

        Draw findDraw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        Member findMember = memberRepository.findByAccountId(accountId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        // 관리자인지 확인
        if (findMember.getAccountType() == AccountType.ADMIN) {

            // 추첨이 완료되었는지 확인
            if (findDraw.getStatus() != DrawStatus.COMPLETED) {

                // draws 테이블에서 추첨 삭제
                drawRepository.delete(findDraw);

                List<CertificateDocs> certificateDocs = certificateDocsRepository.findByMemberAndDrawId(findMember, drawId);

                // 사용자가 추첨 신청할 때 제출했던 문서들도 제거
                for (CertificateDocs certificateDocument : certificateDocs) {
                    objectStorageService.deleteObject(certificateDocument.getFileUrl());
                }
                certificateDocsRepository.deleteAll(certificateDocs);

            } else {
                throw new ExceptionHandler((ErrorStatus.DRAW_STATUS_IS_COMPLETED));
            }
        } else {
            throw new ExceptionHandler((ErrorStatus._UNAUTHORIZED_ACCESS));
        }
    }
}