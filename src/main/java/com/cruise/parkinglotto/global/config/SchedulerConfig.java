package com.cruise.parkinglotto.global.config;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.service.drawService.DrawService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final DrawRepository drawRepository;
    private final DrawService drawService;

    @PostConstruct
    public void scheduleDraws() {
        List<Draw> pendedDrawList = drawRepository.findByStatus(DrawStatus.PENDING);
        List<Draw> openedDrawList = drawRepository.findByStatus(DrawStatus.OPEN);
        for (Draw draw : pendedDrawList)  drawService.openDraw(draw);
        for (Draw draw : openedDrawList) drawService.closeDraw(draw);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("task-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
