package com.grepp.spring.app.model.event.factory;

import com.grepp.spring.app.model.event.strategy.EventCreationStrategy;
import com.grepp.spring.app.model.event.strategy.GroupEventCreationStrategy;
import com.grepp.spring.app.model.event.strategy.SingleEventCreationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventCreationStrategyFactory {

    private final GroupEventCreationStrategy groupEventCreationStrategy;
    private final SingleEventCreationStrategy singleEventCreationStrategy;

    public EventCreationStrategy getStrategy(Long groupId) {
        if (groupId != null) {
            log.debug("그룹 이벤트 전략 - 그룹ID: {}", groupId);
            return groupEventCreationStrategy;
        } else {
            log.debug("일회성 이벤트 전략");
            return singleEventCreationStrategy;
        }
    }
}