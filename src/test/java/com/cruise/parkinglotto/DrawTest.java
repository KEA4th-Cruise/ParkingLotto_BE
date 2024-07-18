package com.cruise.parkinglotto;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.domain.enums.DrawType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DrawTest {
    @Test
    @DisplayName("Draw 객체가 생성되는지 확인하는 테스트")
    void createDraw(){

        Draw draw= Draw.builder()
                .id(32L)
                .type(DrawType.GENERAL)
                .title("DrawClassTest")
                .drawStartAt(LocalDateTime.of(2024, 7, 18,5,16))
                .drawEndAt(LocalDateTime.of(2024, 7, 21,5,16))
                .usageStartAt(LocalDateTime.of(2024, 8,18, 0,0))
                .usageEndAt(LocalDateTime.of(2024, 12, 21,0,0))
                .seedNum("UserSeed")
                .description("DrawDescription")
                .mapImageUrl("https://gcu-sw.agit.io")
                .status(DrawStatus.OPEN)
                .totalSlots(52L)
                .year("2024")
                .quarter("IDK")
                .build();

        Assertions.assertThat(draw.getId()).isEqualTo(32L);
        Assertions.assertThat(draw.getType()).isEqualTo(DrawType.GENERAL);
        Assertions.assertThat(draw.getTitle()).isEqualTo("DrawClassTest");
        Assertions.assertThat(draw.getDrawStartAt()).isEqualTo(LocalDateTime.of(2024, 7, 18,5,16));
        Assertions.assertThat(draw.getDrawEndAt()).isEqualTo(LocalDateTime.of(2024, 7, 21,5,16));
        Assertions.assertThat(draw.getUsageStartAt()).isEqualTo(LocalDateTime.of(2024, 8,18, 0,0));
        Assertions.assertThat(draw.getUsageEndAt()).isEqualTo(LocalDateTime.of(2024, 12, 21,0,0));
        Assertions.assertThat(draw.getSeedNum()).isEqualTo("UserSeed");
        Assertions.assertThat(draw.getDescription()).isEqualTo("DrawDescription");
        Assertions.assertThat(draw.getMapImageUrl()).isEqualTo("https://gcu-sw.agit.io");
        Assertions.assertThat(draw.getStatus()).isEqualTo(DrawStatus.OPEN);
        Assertions.assertThat(draw.getTotalSlots()).isEqualTo(52L);
        Assertions.assertThat(draw.getYear()).isEqualTo("2024");
        Assertions.assertThat(draw.getQuarter()).isEqualTo("IDK");
    }
}
