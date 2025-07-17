package com.grepp.spring.app.model.member.entity;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.mainpage.entity.Calendar;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Members")
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Min(0)
    @Max(7)
    private Integer profileImageNumber;

    @Column(nullable = true, unique = true) // 잠깐 나가있어
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "유효하지 않은 전화번호 형식입니다. (예: 010-1234-5678)")
    private String tel;

    // 멤버가 삭제되면 그 멤버의 소셜 토큰도 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAuthToken> socialAuthTokens = new ArrayList<>();

    // 멤버가 삭제되면 그 멤버의 캘린더도 삭제
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Calendar calendar;

    // 멤버가 삭제되면 그 멤버의 즐겨찾는 장소도 삭제
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private FavoriteLocation favoriteLocation;

    // 멤버가 삭제되면 그 멤버의 즐겨찾는 시간대도 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteTimetable> favoriteTimetables = new ArrayList<>();

    // 멤버가 삭제되면 그룹멤버도 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    // 멤버가 삭제되면 이벤트멤버도 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventMember> eventMembers = new ArrayList<>();

    // 멤버가 삭제되면 스케줄멤버도 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleMember> scheduleMembers = new ArrayList<>();





}
