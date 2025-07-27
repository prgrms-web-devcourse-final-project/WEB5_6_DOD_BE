package com.grepp.spring.app.model.schedule.entity;

import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.infra.entity.BaseEntity;
import com.grepp.spring.infra.error.exceptions.schedule.NotScheduleMasterException;
import com.grepp.spring.infra.response.ScheduleErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ScheduleMembers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleMember extends BaseEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private ScheduleRole role;

    @Column
    private String departLocationName;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // ScheduleMember 가 삭제되면 그 멤버의 투표도 삭제
    @OneToMany(mappedBy = "scheduleMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    // 일정 마스터 권한 부여
    public void grantMasterRole() {
        this.role = ScheduleRole.ROLE_MASTER;
    }

    public void isScheduleMasterOrThrow() {
        if (this.role != ScheduleRole.ROLE_MASTER) {
                throw new NotScheduleMasterException(ScheduleErrorCode.NOT_SCHEDULE_MASTER);
            }
    }

//    public ScheduleRole isCreate(String userId) {
//
//        if (member.getId().equals(userId)) {
//            this.role = ScheduleRole.ROLE_MASTER;
//        }
//        else {
//            this.role = ScheduleRole.ROLE_MEMBER;
//        }
//
//        return role;
//    }

}
