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
}