package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.controller.api.event.payload.response.AllTimeScheduleResponse;
import com.grepp.spring.app.model.event.dto.AllTimeEventDto;
import com.grepp.spring.app.model.event.entity.QEvent;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public abstract class EventQueryRepositoryImpl implements EventQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public AllTimeEventDto findAllTimeEventById(Long eventId) {
        QEvent event = QEvent.event;

        return queryFactory
            .select(Projections.constructor(AllTimeEventDto.class,
                event.id,
                event.title,
                event.description,
                event.maxMember
            ))
            .from(event)
            .where(event.id.eq(eventId))
            .fetchOne();
    }
}
