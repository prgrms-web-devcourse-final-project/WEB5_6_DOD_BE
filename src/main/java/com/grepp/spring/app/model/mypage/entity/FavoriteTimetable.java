package com.grepp.spring.app.model.mypage.entity;

import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "FavoriteTimetables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FavoriteTimetable extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "day", nullable = false)
    private String day; // "mon", "tue", "wed", "thu", "fri", "sat", "sun"

    @Column(name = "time_bit", nullable = false)
    private Long timeBit; // 12자리 16진수 문자열

    public static FavoriteTimetable create(Member member, String day, Long timeBit) {
        return FavoriteTimetable.builder()
            .member(member)
            .day(day)
            .timeBit(timeBit != null ? timeBit : 0L) // null 방지 처리
            .build();
    }

    // XOR 토글 로직
    public boolean toggle(Long newBit) {
        long updated = this.timeBit ^ newBit;
        this.timeBit = updated;
        return updated != 0L; // 0 이면 완전 해제 (삭제), 0 아니면 선택된 블럭 있으니 db 에 유지
    }

}
