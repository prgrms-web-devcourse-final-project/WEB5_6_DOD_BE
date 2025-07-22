package com.grepp.spring.app.model.event.strategy;

import com.grepp.spring.app.model.event.dto.CreateEventDto;
import com.grepp.spring.app.model.event.entity.Event;

public interface EventCreationStrategy {

    Event createEvent(CreateEventDto serviceRequest, String currentMemberId);

}