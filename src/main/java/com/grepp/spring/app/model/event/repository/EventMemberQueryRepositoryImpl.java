package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.dto.AllTimeEventMemberDto;
import com.grepp.spring.app.model.event.entity.QEventMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public abstract class EventMemberQueryRepositoryImpl implements EventMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AllTimeEventMemberDto> findAllTimeEventMembersByEventId(Long eventId) {
        QEventMember eventMember = QEventMember.eventMember;

        return queryFactory
            .select(Projections.constructor(AllTimeEventMemberDto.class,
                eventMember.member.id))
            .from(eventMember)
            .where(eventMember.event.id.eq(eventId))
            .fetch();
    }
}
