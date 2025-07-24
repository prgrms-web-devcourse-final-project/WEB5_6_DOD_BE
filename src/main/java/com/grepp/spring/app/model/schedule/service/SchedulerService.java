package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleCommandRepository scheduleCommandRepository;


    // 왼쪽부터 0초 0분 1시(새벽) 1일 *(매달) *(요일 무시)
    @Scheduled(cron = "0 0 1 1 * *")
    public void schedulingSchedule() {
        scheduleQueryRepository.findAll().forEach(schedule -> {
            if(schedule.getStatus() == ScheduleStatus.COMPLETE) {
                scheduleCommandRepository.delete(schedule);
            }
        });
    }
}
