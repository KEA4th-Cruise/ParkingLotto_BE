package com.cruise.parkinglotto.web.dto.parkingSpaceDTO;
import lombok.Builder;
import lombok.Getter;

public class ParkingSpaceRequestDTO {

    @Getter
    @Builder
    public static class AddParkingSpaceDTO {
        private String address;
        private String name;
        private Long slots;
    }

}
