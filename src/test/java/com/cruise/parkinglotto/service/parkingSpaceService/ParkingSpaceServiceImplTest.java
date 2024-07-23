package com.cruise.parkinglotto.service.parkingSpaceService;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.aws.AmazonConfig;
import com.cruise.parkinglotto.global.aws.AmazonS3Manager;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSpaceServiceImplTest {


    @Mock
    private ParkingSpaceRepository parkingSpaceRepository;

    @Mock
    private DrawRepository drawRepository;

    @Mock
    private AmazonS3Manager amazonS3Manager;

    @Mock
    private AmazonConfig amazonConfig;

    @InjectMocks
    private ParkingSpaceServiceImpl parkingSpaceService;

    private Draw draw;
    private MultipartFile multipartFile;
    private ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDTO;

    @BeforeEach
    public void setUp() throws IOException {
        draw = Draw.builder()
                .id(1L)
                .title("Test Draw")
                .build();
        multipartFile = mock(MultipartFile.class);
        addParkingSpaceDTO = ParkingSpaceRequestDTO.AddParkingSpaceDTO.builder()
                .name("Test Parking Space")
                .slots(10)
                .build();

        when(drawRepository.findById(1L)).thenReturn(Optional.of(draw));
    }

    @Test
    @DisplayName("해당 추첨에 주차 구역 추가")
    public void testAddParkingSpace() {
        when(amazonConfig.getParkingSpaceImagePath()).thenReturn("test/path/");
        when(amazonS3Manager.uploadFileToDirectory(anyString(), anyString(), any(MultipartFile.class)))
                .thenReturn("http://s3.aws/test/path/Test_Draw_Test_Parking_Space");
        when(parkingSpaceRepository.save(any(ParkingSpace.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSpace result = parkingSpaceService.addParkingSpace(1L, multipartFile, addParkingSpaceDTO);

        assertNotNull(result);
        assertEquals("http://s3.aws/test/path/Test_Draw_Test_Parking_Space", result.getFloorPlanImageUrl());
        assertEquals(draw, result.getDraw());
        verify(parkingSpaceRepository, times(1)).save(any(ParkingSpace.class));
    }

    @Test
    @DisplayName("추첨이 없을 경우 예외 처리")
    public void testAddParkingSpace_DrawNotFound() {
        when(drawRepository.findById(1L)).thenReturn(Optional.empty());

        ExceptionHandler exception = assertThrows(ExceptionHandler.class, () -> {
            parkingSpaceService.addParkingSpace(1L, multipartFile, addParkingSpaceDTO);
        });


        assertEquals(ErrorStatus.DRAW_NOT_FOUND, exception.getCode());
        verify(parkingSpaceRepository, never()).save(any(ParkingSpace.class));
    }

    @Test
    @DisplayName("S3 버킷 업로드 불가")
    public void testAddParkingSpace_AmazonS3Exception() {
        when(amazonConfig.getParkingSpaceImagePath()).thenReturn("test/path/");
        when(amazonS3Manager.uploadFileToDirectory(anyString(), anyString(), any(MultipartFile.class)))
                .thenThrow(new AmazonS3Exception("S3 Upload Failed"));

        AmazonS3Exception exception = assertThrows(AmazonS3Exception.class, () -> {
            parkingSpaceService.addParkingSpace(1L, multipartFile, addParkingSpaceDTO);
        });

        assertThat(exception.getMessage()).contains("S3 Upload Failed");
        verify(parkingSpaceRepository, never()).save(any(ParkingSpace.class));
    }
}