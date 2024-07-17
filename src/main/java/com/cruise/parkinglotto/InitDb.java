package com.cruise.parkinglotto;


import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.AccountType;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


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

        public void dbInit1() {


            Member member = createMember("최준범", "cjb", "111", "developer", "example1@example.com", AccountType.USER, EnrollmentStatus.ENROLLED, "1234", "Seoul", WorkType.TYPE1, "hello", 50L, 30L, 30.00, 0L);
            em.persist(member);
            Applicant applicant = createApplicant(50.0,WinningStatus.WINNER,1L,0L,100L,"100","150",1L,2L, 30.00,WorkType.TYPE1,50L,30L,0L,member,null);
            em.persist(applicant);
            ParkingSpace parkingSpace = createParkingSpace("A","seoul",100L,50L,"space A",40L,null);
            em.persist(parkingSpace);

        }


        public void dbInit2() {
            Member member = createMember("준범최", "cjbbb", "112", "marketer", "example2@example.com", AccountType.USER, EnrollmentStatus.ENROLLED, "1236", "Busan", WorkType.TYPE2, "nice", 200L, 160L, 300.00, 0L);
            em.persist(member);
            Applicant applicant = createApplicant(50.0,WinningStatus.WINNER,2L,0L,100L,"100","150",1L,2L, 300.00,WorkType.TYPE2,200L,160L,0L,member,null);
            em.persist(applicant);
            ParkingSpace parkingSpace = createParkingSpace("B","seoul",100L,50L,"space B",40L,null);
            em.persist(parkingSpace);

        }

        private Applicant createApplicant(Double weightedTotalScore, WinningStatus winningStatus, Long parkingSpaceId, Long reserveNum, Long userSeedIndex, String userSeed, String randomNumber, Long firstChoice, Long secondChoice, Double distance, WorkType workType, Long trafficCommuteTime, Long carCommuteTime, Long recentLossCount, Member member, Draw draw) {

           return Applicant.builder()
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
                    .member(member).build();

        }

        private Member createMember(String nameKo, String accountId, String employeeNo, String deptPathName, String email, AccountType accountType, EnrollmentStatus enrollmentStatus, String carNum, String address, WorkType workType, String nickName, Long trafficCommuteTime, Long carCommuteTime, Double distance, Long recentLossCount) {
            return Member.builder( )
                    .nameKo(nameKo)
                    .accountId(accountId)
                    .employeeNo(employeeNo)
                    .deptPathName(deptPathName)
                    .email(email)
                    .accountType(accountType)
                    .enrollmentStatus(enrollmentStatus)
                    .carNum(carNum)
                    .address(address)
                    .workType(workType)
                    .trafficCommuteTime(trafficCommuteTime)
                    .carCommuteTime(carCommuteTime)
                    .distance(distance)
                    .recentLossCount(recentLossCount)
                    .build( );

        }

        private ParkingSpace createParkingSpace( String name, String address, Long slots, Long remainSlots, String floorPlanImageUrl, Long applicantCount, Draw draw )  {

            return ParkingSpace.builder( )
                    .name(name)
                    .address(address)
                    .slots(slots)
                    .remainSlots(remainSlots)
                    .floorPlanImageUrl(floorPlanImageUrl)
                    .applicantCount(applicantCount)
                    .build( );

        }

    }


}
