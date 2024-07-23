package com.cruise.parkinglotto;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.repository.DrawRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DrawRepositoryTest {


    @Autowired
    DrawRepository drawRepository;

    @Test
    @DisplayName("Draw 저장하기")
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

        // when
        Draw savedDraw = drawRepository.save(draw);
        Draw savedDraw2 = drawRepository.save(draw2);

        // then
        Optional<Draw> result = drawRepository.findById(savedDraw.getId());
        Assertions.assertTrue(result.isPresent(), "Draw should be present");
        Assertions.assertEquals(savedDraw.getId(), result.get().getId());
        Assertions.assertEquals(savedDraw.getTitle(), result.get().getTitle());
        Assertions.assertEquals(savedDraw.getType(), result.get().getType());

        Optional<Draw> result2 = drawRepository.findById(savedDraw2.getId());
        Assertions.assertTrue(result2.isPresent(), "Draw2 should be present");
        Assertions.assertEquals(savedDraw2.getId(), result2.get().getId());
        Assertions.assertEquals(savedDraw2.getTitle(), result2.get().getTitle());
        Assertions.assertEquals(savedDraw2.getType(), result2.get().getType());
    }

}
