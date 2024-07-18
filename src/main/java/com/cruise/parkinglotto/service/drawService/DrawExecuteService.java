package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.web.dto.DrawResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface DrawExecuteService {
    //추첨의 시드 번호를 저장
    void updateSeedNum(Long drawId);
    //신청자들의 난수 생성 및 할당
    void assignRandomNumber(List<Applicant> applicants, String seed);
}
