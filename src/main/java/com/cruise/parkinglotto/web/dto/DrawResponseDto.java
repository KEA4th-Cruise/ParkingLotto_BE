package com.cruise.parkinglotto.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class DrawResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWinnerPersonDto {
        private Long drawId;
        private String name_ko;
        private String account_id;
        private String depth_path_name;
        private Long reserve_num;
        private Double weighted_total_score;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDrawResultPersonListDto{
        private List<GetWinnerPersonDto> drawResultDtoList;
    }
}
