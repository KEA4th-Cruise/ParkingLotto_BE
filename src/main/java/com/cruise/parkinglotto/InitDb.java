package com.cruise.parkinglotto;


import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {

        initService.dbInit1( );
        initService.dbInit2( );

    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final BCryptPasswordEncoder bCryptPasswordEncoder;


        public void dbInit1() {

            LocalDateTime drawStartAt = LocalDateTime.now( );
            Draw draw = createDraw(DrawType.GENERAL, "2024년도 1회차 주차추첨", drawStartAt, drawStartAt, drawStartAt, drawStartAt, "100", "추첨", "mapImageUrl", DrawStatus.OPEN, 100, "2024", "1", null);
            em.persist(draw);
            Member member = createMember("최준범", "cjb", "111", "developer", "example1@example.com", AccountType.USER, EnrollmentStatus.ENROLLED, "1234", "Seoul", WorkType.TYPE1, "hello", 50, 30, 30.00, 0, bCryptPasswordEncoder.encode("11111"));
            em.persist(member);

            Applicant applicant = createApplicant(50.0, WinningStatus.WINNER, 1L, 0, 100, "A", 150.00, 1L, 2L, 30.00, WorkType.TYPE1, 50, 30, 0, member, draw);

            em.persist(applicant);
            ParkingSpace parkingSpace = createParkingSpace("A", "seoul", 100, 50, "space A", 40, draw);
            em.persist(parkingSpace);

        }


        public void dbInit2() {


            LocalDateTime drawStartAt = LocalDateTime.now( );
            Draw draw = createDraw(DrawType.GENERAL, "2024년도 1회차 주차추첨", drawStartAt, drawStartAt, drawStartAt, drawStartAt, "100", "추첨", "mapImageUrl", DrawStatus.OPEN, 100, "2024", "2", null);

            em.persist(draw);
            Member member = createMember("준범최", "cjbbb", "112", "marketer", "example2@example.com", AccountType.USER, EnrollmentStatus.ENROLLED, "1236", "Busan", WorkType.TYPE2, "nice", 200, 160, 300.00, 0, bCryptPasswordEncoder.encode("11112"));
            em.persist(member);
            Applicant applicant = createApplicant(50.0, WinningStatus.WINNER, 2L, 0, 100, "B", 150.00, 1L, 2L, 300.00, WorkType.TYPE2, 200, 160, 0, member, draw);

            em.persist(applicant);
            ParkingSpace parkingSpace = createParkingSpace("B", "seoul", 100, 50, "space B", 40, draw);
            em.persist(parkingSpace);

        }


        private Applicant createApplicant(Double weightedTotalScore, WinningStatus winningStatus, Long parkingSpaceId, Integer reserveNum, Integer userSeedIndex, String userSeed, Double randomNumber, Long firstChoice, Long secondChoice, Double distance, WorkType workType, Integer trafficCommuteTime, Integer carCommuteTime, Integer recentLossCount, Member member, Draw draw) {

            return Applicant.builder( )
                    .weightedTotalScore(weightedTotalScore)
                    .winningStatus(winningStatus)
                    .parkingSpaceId(parkingSpaceId)
                    .reserveNum(reserveNum)
                    .userSeedIndex(userSeedIndex)
                    .userSeed(userSeed)
                    .randomNumber(randomNumber)
                    .firstChoice(firstChoice)
                    .secondChoice(secondChoice)
                    .distance(distance)
                    .workType(workType)
                    .trafficCommuteTime(trafficCommuteTime)
                    .carCommuteTime(carCommuteTime)
                    .recentLossCount(recentLossCount)
                    .member(member)
                    .draw(draw).build( );

        }

        private Member createMember(String nameKo, String accountId, String employeeNo, String deptPathName, String email, AccountType accountType, EnrollmentStatus enrollmentStatus, String carNum, String address, WorkType workType, String nickName, Integer trafficCommuteTime, Integer carCommuteTime, Double distance, Integer recentLossCount, String password) {
            return Member.builder( )
                    .nameKo(nameKo)
                    .accountId(accountId)
                    .employeeNo(employeeNo)
                    .deptPathName(deptPathName)
                    .email(email)
                    .accountType(accountType)
                    .enrollmentStatus(enrollmentStatus)
                    .carNum(carNum)
                    .password(password)
                    .build( );

        }

        private ParkingSpace createParkingSpace(String name, String address, Integer slots, Integer remainSlots, String floorPlanImageUrl, Integer applicantCount, Draw draw) {

            return ParkingSpace.builder( )
                    .name(name)
                    .address(address)
                    .slots(slots)
                    .remainSlots(remainSlots)
                    .floorPlanImageUrl(floorPlanImageUrl)
                    .applicantCount(applicantCount)
                    .draw(draw)
                    .build( );

        }

        private Draw createDraw(DrawType type, String title, LocalDateTime drawStartAt, LocalDateTime drawEndAt, LocalDateTime usageStartAt, LocalDateTime usageEndAt, String seedNum, String description, String mapImageUrl, DrawStatus status, Integer totalSlots, String year, String quarter, DrawStatistics drawStatistics) {

            return Draw.builder( )
                    .type(type)
                    .title(title)
                    .drawStartAt(drawStartAt)
                    .drawEndAt(drawEndAt)
                    .usageStartAt(usageStartAt)
                    .usageEndAt(usageEndAt)
                    .seedNum(seedNum)
                    .description(description)
                    .mapImageUrl(mapImageUrl)
                    .status(status)
                    .totalSlots(totalSlots)
                    .year(year)
                    .quarter(quarter)
                    .drawStatistics(drawStatistics)
                    .build( );
        }

    }


}

