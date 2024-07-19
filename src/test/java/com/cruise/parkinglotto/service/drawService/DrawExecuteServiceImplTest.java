package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.MemberRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DrawExecuteServiceImplTest {

    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private DrawRepository drawRepository;

    @Mock
    private ParkingSpaceRepository parkingSpaceRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private DrawExecuteServiceImpl drawExecuteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("추첨 실행 테스트")
    @Test
    void testExecuteDraw() {
        Long drawId = 1L;
        String seed = "testSeed";
        Draw draw = Draw.builder()
                .id(drawId)
                .seedNum(seed)
                .build();
        when(drawRepository.findById(drawId)).thenReturn(Optional.of(draw));

        Member member1 = Member.builder().id(1L).build();
        Member member2 = Member.builder().id(2L).build();

        List<Applicant> applicants = new ArrayList<>();
        applicants.add(Applicant.builder().id(1L).weightedTotalScore(12.0).firstChoice(2L).secondChoice(1L).randomNumber(0.5).member(member1).build());
        applicants.add(Applicant.builder().id(2L).weightedTotalScore(82.0).firstChoice(1L).secondChoice(2L).randomNumber(0.8).member(member2).build());
        when(applicantRepository.findByDrawId(drawId)).thenReturn(applicants);

        List<ParkingSpace> parkingSpaces = new ArrayList<>();
        parkingSpaces.add(ParkingSpace.builder().id(1L).draw(draw).slots(10L).remainSlots(10L).build());
        parkingSpaces.add(ParkingSpace.builder().id(2L).draw(draw).slots(10L).remainSlots(10L).build());
        when(parkingSpaceRepository.findByDrawId(drawId)).thenReturn(parkingSpaces);

        // Mocking the repository update methods
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            applicants.replaceAll(a -> a.getId().equals(id) ? Applicant.builder()
                    .id(a.getId())
                    .weightedTotalScore(a.getWeightedTotalScore())
                    .firstChoice(a.getFirstChoice())
                    .secondChoice(a.getSecondChoice())
                    .randomNumber(a.getRandomNumber())
                    .member(a.getMember())
                    .reserveNum(0L)
                    .parkingSpaceId(a.getParkingSpaceId())
                    .build() : a);
            return null;
        }).when(applicantRepository).updateReserveNum(anyLong(), eq(0));

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Long parkingSpaceId = invocation.getArgument(1);
            applicants.replaceAll(a -> a.getId().equals(id) ? Applicant.builder()
                    .id(a.getId())
                    .weightedTotalScore(a.getWeightedTotalScore())
                    .firstChoice(a.getFirstChoice())
                    .secondChoice(a.getSecondChoice())
                    .randomNumber(a.getRandomNumber())
                    .member(a.getMember())
                    .reserveNum(a.getReserveNum())
                    .parkingSpaceId(parkingSpaceId)
                    .build() : a);
            return null;
        }).when(applicantRepository).updateParkingSpaceId(anyLong(), anyLong());

        drawExecuteService.executeDraw(drawId);

        verify(drawRepository, atLeastOnce()).findById(drawId);
        verify(applicantRepository, atLeastOnce()).findByDrawId(drawId);
        verify(parkingSpaceRepository, atLeastOnce()).findByDrawId(drawId);

        // Additional verification to ensure methods were called
        verify(applicantRepository, atLeastOnce()).assignRandomNumber(anyLong(), anyDouble());
        verify(applicantRepository, atLeastOnce()).updateReserveNum(anyLong(), eq(0));
        verify(memberRepository, atLeastOnce()).resetRecentLossCount(anyLong());
        verify(applicantRepository, atLeastOnce()).updateParkingSpaceId(anyLong(), anyLong());
        verify(parkingSpaceRepository, atLeastOnce()).decrementRemainSlots(anyLong());

        // Output the state of each applicant to check values
        for (Applicant applicant : applicants) {
            System.out.println("Applicant ID: " + applicant.getId());
            System.out.println("Random Number: " + applicant.getRandomNumber());
            System.out.println("First Choice: " + applicant.getFirstChoice());
            System.out.println("Second Choice: " + applicant.getSecondChoice());
            System.out.println("Assigned Parking Space ID: " + applicant.getParkingSpaceId());
            System.out.println("Reserve Number: " + applicant.getReserveNum());
            System.out.println("-----");
        }
    }

    @DisplayName("시드번호 생성 확인")
    @Test
    void updateSeedNum() {
        //given
        Long drawId = 1L;
        Draw draw = Draw.builder()
                .id(drawId)
                .build();
        when(drawRepository.findById(drawId)).thenReturn(Optional.of(draw));

        Applicant applicant1 = Applicant.builder()
                .userSeed("seed1")
                .build();

        Applicant applicant2 = Applicant.builder()
                .userSeed("seed2")
                .build();
        List<Applicant> applicants = Arrays.asList(applicant1, applicant2);

        //when
        when(applicantRepository.findByDrawId(drawId)).thenReturn(applicants);
        drawExecuteService.updateSeedNum(drawId);
        String expectedSeed = "seed1seed2";

        //then
        verify(drawRepository, times(1)).updateSeedNum(eq(drawId), eq(expectedSeed));
    }

    @DisplayName("난수 생성 테스트")
    @Test
    public void testAssignRandomNumber() throws Exception {
        Long drawId = 1L;
        String seed = "testSeed";
        Applicant applicant1 = Applicant.builder().id(1L).userSeed("seed1").build();
        Applicant applicant2 = Applicant.builder().id(2L).userSeed("seed2").build();
        Applicant applicant3 = Applicant.builder().id(3L).userSeed("seed3").build();

        List<Applicant> applicants = Arrays.asList(applicant1, applicant2, applicant3);

        when(applicantRepository.findByDrawId(drawId)).thenReturn(applicants);

        // Mock the assignRandomNumber method
        doNothing().when(applicantRepository).assignRandomNumber(anyLong(), anyDouble());

        drawExecuteService.assignRandomNumber(drawId, seed);

        // Verify and compare the actual random numbers
        Random rand = new Random(seed.hashCode());
        double firstRandomNumber = rand.nextDouble();
        System.out.println("Applicant 1 ID: " + applicant1.getId() + ", Random Number: " + firstRandomNumber);
        verify(applicantRepository).assignRandomNumber(applicant1.getId(), firstRandomNumber);

        rand = new Random(Double.doubleToLongBits(firstRandomNumber));
        double secondRandomNumber = rand.nextDouble();
        System.out.println("Applicant 2 ID: " + applicant2.getId() + ", Random Number: " + secondRandomNumber);
        verify(applicantRepository).assignRandomNumber(applicant2.getId(), secondRandomNumber);

        rand = new Random(Double.doubleToLongBits(secondRandomNumber));
        double thirdRandomNumber = rand.nextDouble();
        System.out.println("Applicant 3 ID: " + applicant3.getId() + ", Random Number: " + thirdRandomNumber);
        verify(applicantRepository).assignRandomNumber(applicant3.getId(), thirdRandomNumber);

        // Verify that the generated numbers match the expected sequence
        Random randTest = new Random(seed.hashCode());
        double firstTestRandomNumber = randTest.nextDouble();
        assertEquals(firstTestRandomNumber, firstRandomNumber, 0.0000001);

        randTest = new Random(Double.doubleToLongBits(firstTestRandomNumber));
        double secondTestRandomNumber = randTest.nextDouble();
        assertEquals(secondTestRandomNumber, secondRandomNumber, 0.0000001);

        randTest = new Random(Double.doubleToLongBits(secondTestRandomNumber));
        double thirdTestRandomNumber = randTest.nextDouble();
        assertEquals(thirdTestRandomNumber, thirdRandomNumber, 0.0000001);
    }

    @DisplayName("가중치 계산 확인")
    @Test
    void calculateWeight() {
        //given
        Member member = Member.builder()
                .id(1L)
                .workType(WorkType.TYPE1)
                .recentLossCount(3L)
                .build();

        Applicant applicant = Applicant.builder()
                .id(1L)
                .member(member)
                .trafficCommuteTime(45L)
                .carCommuteTime(30L)
                .distance(15.0)
                .build();

        // 가중치 예상값 계산
        double expectedWeight = 25 // workType "TYPE1"
                + (10 + 9 * (1 - Math.exp(-0.2 * 45))) // trafficCommuteTime 45
                + (5 * (1 - Math.exp(-0.05 * 30))) // carCommuteTime 30
                + (5 * (1 - Math.exp(-0.05 * Math.abs(45 - 30)))) // commuteTimeDiff
                + (20 * (1 - Math.exp(-0.02 * 15))) // distance
                + (10 * (1 - Math.exp(-0.3 * 3))); // recentLossCount

        //when
        drawExecuteService.calculateWeight(applicant);

        // ArgumentCaptor를 사용하여 updateWeightedTotalScore 메서드 호출 시 전달된 인수를 캡처합니다.
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Double> weightCaptor = ArgumentCaptor.forClass(Double.class);

        verify(applicantRepository).updateWeightedTotalScore(idCaptor.capture(), weightCaptor.capture());

        //then
        assertEquals(applicant.getId(), idCaptor.getValue());
        assertEquals(expectedWeight, weightCaptor.getValue());
    }

    @DisplayName("예비번호 부여 로직 확인")
    @Test
    void assignWaitlistNumbers() {
        //given
        List<Applicant> applicants = new ArrayList<>();
        Member member1 = Member.builder().id(1L).build();
        Member member2 = Member.builder().id(2L).build();
        Member member3 = Member.builder().id(3L).build();

        applicants.add(Applicant.builder().id(1L).member(member1).reserveNum(0L).build()); // 당첨자
        applicants.add(Applicant.builder().id(2L).member(member2).reserveNum(1L).build()); // 당첨자 아님
        applicants.add(Applicant.builder().id(3L).member(member3).reserveNum(1L).build()); // 당첨자 아님

        //when
        drawExecuteService.assignWaitListNumbers(applicants);

        // ArgumentCaptor를 사용하여 updateReserveNum 메서드 호출 시 전달된 인수를 캡처합니다.
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> reserveNumCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(applicantRepository, times(2)).updateReserveNum(idCaptor.capture(), reserveNumCaptor.capture());

        //then
        List<Long> capturedIds = idCaptor.getAllValues();
        List<Integer> capturedReserveNums = reserveNumCaptor.getAllValues();

        assertEquals(2, capturedIds.size());
        assertEquals(2, capturedReserveNums.size());

        // 예비번호가 1부터 순차적으로 부여되었는지 확인합니다.
        assertEquals(applicants.get(1).getId(), capturedIds.get(0));
        assertEquals(1, capturedReserveNums.get(0));

        assertEquals(applicants.get(2).getId(), capturedIds.get(1));
        assertEquals(2, capturedReserveNums.get(1));
    }

}
