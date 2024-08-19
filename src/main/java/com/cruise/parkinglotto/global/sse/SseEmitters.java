package com.cruise.parkinglotto.global.sse;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.converter.ParkingSpaceConverter;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseEmitters {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private static final AtomicLong counter = new AtomicLong();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicantRepository applicantRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;

    public SseEmitter add(SseEmitter emitter) {
        this.emitters.add(emitter);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(emitter);    // 만료되면 리스트에서 삭제
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    @Async
    public void realTimeDrawInfo(Draw draw) {
        Integer applicantsCount = applicantRepository.countByDrawId(draw.getId());
        Integer totalSlots = draw.getTotalSlots();
        List<ParkingSpace> parkingSpaceList = parkingSpaceRepository.findByDrawId(draw.getId());
        List<ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO> parkingSpaceCompetitionRateDTOList = parkingSpaceList.stream()
                .map(parkingSpace -> {
                    Integer applicantsCountPerParkingSpace = applicantRepository.countByDrawIdAndFirstChoice(draw.getId(), parkingSpace.getId());
                    return ParkingSpaceConverter.toParkingSpaceCompetitionRateDTO(parkingSpace, applicantsCountPerParkingSpace);
                }).toList();
        DrawResponseDTO.RealTimeDrawInfo realTimeDrawInfo = DrawResponseDTO.RealTimeDrawInfo.builder()
                .applicantsCount(applicantsCount)
                .totalSlots(totalSlots)
                .parkingSpaceCompetitionRateList(parkingSpaceCompetitionRateDTOList)
                .build();
        sendEvent("realTimeDrawInfo", realTimeDrawInfo);
    }

    public void sendEvent(String eventName, Object data) {
        String jsonData = convertToJson(data);
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(jsonData));
            } catch (IOException e) {
                log.error("Error sending event to emitter", e);
                emitter.completeWithError(e);
                this.emitters.remove(emitter);
            }
        });
    }

    private String convertToJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON", e);
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
}
