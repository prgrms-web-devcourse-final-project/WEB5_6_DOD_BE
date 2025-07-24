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
import com.grepp.spring.infra.error.exceptions.member.InvalidNameException;
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
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;


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


    // 객체지향적 설계로 변경하는 과정입니다...
    // 이름을 수정하는 메서드
    public void updateName(String newName) {
        if (StringUtils.hasText(newName)) {
            newName = newName.trim();
            validateName(newName);
            this.name = newName;
        }
    }

    // 이름 유효성 검증은 Member 엔티티 내부 메서드로 이동
    private void validateName(String username){
        if (username == null) {
            throw new InvalidNameException("이름은 필수 입력값입니다.");
        }
        if (username.length() < 2 || username.length() > 10) {
            throw new InvalidNameException("이름은 2자 이상 10자 이하로만 가능합니다.");
        }
        String pattern = "^[가-힣a-zA-Z](?:[가-힣a-zA-Z ]*[가-힣a-zA-Z])?$";
        if (!username.matches(pattern)) {
            throw new InvalidNameException("이름은 한글, 영문만 사용 가능하며, 숫자나 특수문자는 포함할 수 없습니다.");
        }
    }

    // 프로필 이미지를 수정하는 메서드
    public void updateProfileImage() {
        int currentProfileNumber = this.profileImageNumber;
        int newProfileNumber;
        Random random = new Random();
        do {
            newProfileNumber = random.nextInt(8);
        } while (newProfileNumber == currentProfileNumber);
        this.profileImageNumber = newProfileNumber;
    }
}
