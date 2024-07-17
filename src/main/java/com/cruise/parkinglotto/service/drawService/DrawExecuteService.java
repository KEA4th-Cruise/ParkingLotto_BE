package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.web.dto.DrawResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public interface DrawExecuteService {
    void updateSeedNum(Long drawId);
}
