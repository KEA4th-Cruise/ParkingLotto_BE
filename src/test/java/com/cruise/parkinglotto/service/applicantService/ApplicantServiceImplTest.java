package com.cruise.parkinglotto.service.applicantService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicantServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicantRepository applicantRepository;

    @InjectMocks
    private ApplicantServiceImpl applicantService;

    private Applicant applicant;
    private Member member;

    @BeforeEach
    public void setUp() throws IOException {
        member = Member.builder()
                .id(1L)
                .build();
        applicant = Applicant.builder()
                .id(1L)
                .winningStatus(WinningStatus.WINNER)
                .member(member)
                .parkingSpaceId(1L)
                .build();

//        when(memberRepository.save(member)).thenReturn(member);
//        when(applicantRepository.save(applicant)).thenReturn(applicant);
        when(applicantRepository.findByMember(1L)).thenReturn(Optional.of(applicant.getId()));
        when(applicantRepository.findById(1L)).thenReturn(Optional.of(applicant));
    }

    @Test
    @DisplayName("당첨 포기 기능 테스트")
    public void testWinningCancel() {
        // Given
        Long memberId = 1L;

        // When
        applicantService.giveUpMyWinning(memberId);

        // Then
        verify(applicantRepository, times(1)).findByMember(memberId);
        verify(applicantRepository, times(1)).findById(1L);
//        verify(applicantRepository, times(1)).save(applicant);

        Applicant findApplicant = applicantRepository.findById(1L)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));

        assertNotNull(findApplicant);
        assertEquals(null,findApplicant.getParkingSpaceId());
        assertEquals(WinningStatus.CANCELED, findApplicant.getWinningStatus());
    }
}
