package com.grepp.spring.app.model.schedule.entity;

import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.infra.entity.BaseEntity;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Locations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location extends BaseEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String name;

//    @Column
//    private String suggestedMemberId;

    @Column
    private Long voteCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // 장소가 삭제되면 그 장소에 대한 투표도 삭제
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    // 장소가 삭제되면 그 장소에 대한 환승정보도 삭제
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetroTransfer> metroTransfers = new ArrayList<>();

}
