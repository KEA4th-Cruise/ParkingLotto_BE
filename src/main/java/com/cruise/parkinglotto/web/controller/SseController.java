package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.sse.SseEmitters;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitters sseEmitters;

    @Operation(summary = "[메인페이지용] SSE 연결 API", description = "실시간 신청 현황 조회를 위해 해당 API를 요청하여 SSE 연결이 우선적으로 이루어져야합니다.")
    @GetMapping(value = "/connection", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect() {
        SseEmitter emitter = new SseEmitter(60 * 1000L);    //  만료시간을 기본 60초로 설정한다.
        sseEmitters.add(emitter);
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .header("X-Accel-Buffering", "no") // Nginx 사용 시 버퍼링 방지
                .body(emitter);
    }
}
