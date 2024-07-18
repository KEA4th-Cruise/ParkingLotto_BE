package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DrawExecuteServiceImplTest {

    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private DrawRepository drawRepository;

    @InjectMocks
    private DrawExecuteServiceImpl drawExecuteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("시드번호 생성 확인")
    @Test
    void updateSeedNum() {
        //given
        Long drawId = 1L;
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
        verify(drawRepository, times(1)).updateSeedNum(drawId, expectedSeed);
    }

    @DisplayName("난수 생성 테스트")
    @Test
    public void testAssignRandomNumber() throws Exception {
        String seed = "testSeed123";
        Applicant applicant1 = Applicant.builder().id(1L).userSeed("seed1").build();
        Applicant applicant2 = Applicant.builder().id(2L).userSeed("seed2").build();
        Applicant applicant3 = Applicant.builder().id(3L).userSeed("seed3").build();

        List<Applicant> applicants = Arrays.asList(applicant1, applicant2, applicant3);

        // Mock the assignRandomNumber method
        doNothing().when(applicantRepository).assignRandomNumber(anyLong(), anyDouble());

        drawExecuteService.assignRandomNumber(applicants, seed);

        // Verify and compare the actual random numbers
        Random rand = new Random(seed.hashCode());
        double firstRandomNumber = rand.nextDouble();
        System.out.println("신청자 1 ID : " + applicant1.getId() + ", 난수 : " + firstRandomNumber);
        verify(applicantRepository).assignRandomNumber(applicant1.getId(), firstRandomNumber);

        rand = new Random(Double.doubleToLongBits(firstRandomNumber));
        double secondRandomNumber = rand.nextDouble();
        System.out.println("Applicant 2 ID: " + applicant2.getId() + ", Random Number: " + secondRandomNumber);
        verify(applicantRepository).assignRandomNumber(applicant2.getId(), secondRandomNumber);

        rand = new Random(Double.doubleToLongBits(secondRandomNumber));
        double thirdRandomNumber = rand.nextDouble();
        System.out.println("Applicant 3 ID: " + applicant3.getId() + ", Random Number: " + thirdRandomNumber);
        verify(applicantRepository).assignRandomNumber(applicant3.getId(), thirdRandomNumber);

        // Verify that the generated numbers match
        Random randTest = new Random(seed.hashCode());
        double firstTestRandomNumber = randTest.nextDouble();
        assertEquals(firstTestRandomNumber, firstRandomNumber, "First random number does not match");

        randTest = new Random(Double.doubleToLongBits(firstTestRandomNumber));
        double secondTestRandomNumber = randTest.nextDouble();
        assertEquals(secondTestRandomNumber, secondRandomNumber, "Second random number does not match");

        randTest = new Random(Double.doubleToLongBits(secondTestRandomNumber));
        double thirdTestRandomNumber = randTest.nextDouble();
        assertEquals(thirdTestRandomNumber, thirdRandomNumber, "Third random number does not match");
    }
}
