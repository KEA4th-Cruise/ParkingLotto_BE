package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

//    @Query("select ps from ParkingSpace ps where ps. ")
//    ParkingSpace findByMemberId(Long memberId);

}
