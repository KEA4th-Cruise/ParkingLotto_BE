package com.cruise.parkinglotto.global.sse;

import lombok.extern.slf4j.Slf4j;
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
public class SseEmitters {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private static final AtomicLong counter = new AtomicLong();
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    //  SSE 연결 테스트를 위한 sample 메서드
    public void count() {
        long count = counter.incrementAndGet();
        sendEvent("count", count);
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

    public void sendMultipleEvents(String eventName, List<Object> dataList) {
        dataList.forEach(data -> sendEvent(eventName, data));
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
