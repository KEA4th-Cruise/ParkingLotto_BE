package com.cruise.parkinglotto;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DrawServiceImplTest {

    @Autowired
    DrawRepository drawRepository;

    @Autowired
    ParkingSpaceRepository parkingSpaceRepository;

    @Autowired
    DrawService drawService;

    @Test
    @DisplayName("DrawService")
    void createAndFindDraw() {
        // given
        Draw draw = Draw.builder()
                .type(DrawType.GENERAL)
                .title("DrawClassTest")
                .drawStartAt(LocalDateTime.of(2024, 7, 18, 5, 16))
                .drawEndAt(LocalDateTime.of(2024, 7, 21, 5, 16))
                .usageStartAt(LocalDateTime.of(2024, 8, 18, 0, 0))
                .usageEndAt(LocalDateTime.of(2024, 12, 21, 0, 0))
                .seedNum("UserSeed")
                .description("DrawDescription")
                .mapImageUrl("https://gcu-sw.agit.io")
                .status(DrawStatus.OPEN)
                .totalSlots(52)
                .year("2024")
                .quarter("IDK")
                .build();

        Draw draw2 = Draw.builder()
                .id(14L)
                .type(DrawType.PRIORITY)
                .title("DrawClassTest2")
                .drawStartAt(LocalDateTime.of(2026, 8, 19, 5, 16))
                .drawEndAt(LocalDateTime.of(2026, 9, 22, 5, 16))
                .usageStartAt(LocalDateTime.of(2026, 10, 19, 0, 0))
                .usageEndAt(LocalDateTime.of(2026, 11, 22, 0, 0))
                .seedNum("UserSeed2")
                .description("DrawDescription2")
                .mapImageUrl("https://gcu-sw.agit.io")
                .status(DrawStatus.PENDING)
                .totalSlots(53)
                .year("2026")
                .quarter("IDK2")
                .build();

        ParkingSpace parkingSpace = ParkingSpace.builder()
                .name("test")
                .address("testAddress")
                .slots(34)
                .remainSlots(35)
                .floorPlanImageUrl("https://gcu-sw.agit.io1")
                .applicantCount(36)
                .draw(draw)
                .build();


        // when
        Draw savedDraw = drawRepository.save(draw);
        Draw savedDraw2 = drawRepository.save(draw2);
        ParkingSpace saveParkingSpace=parkingSpaceRepository.save(parkingSpace);



        HttpServletRequest httpServletRequest = null;
        DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfoDto = drawService.getCurrentDrawInfo(httpServletRequest, draw.getId());

        assertEquals(getCurrentDrawInfoDto.getMapImageUrl(), draw.getMapImageUrl());
    }
}

