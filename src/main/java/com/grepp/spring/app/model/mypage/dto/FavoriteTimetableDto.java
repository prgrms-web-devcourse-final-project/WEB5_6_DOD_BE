package com.grepp.spring.app.model.mypage.dto;


import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteTimetableDto {
  private Long favoriteTimetableId;
  private String timeBitMon;
  private String timeBitTue;
  private String timeBitWed;
  private String timeBitThu;
  private String timeBitFri;
  private String timeBitSat;
  private String timeBitSun;


  // Entity → DTO
  public static FavoriteTimetableDto fromEntity(FavoriteTimetable entity) {
    return FavoriteTimetableDto.builder()
        .favoriteTimetableId(entity.getId())
        .timeBitMon(entity.getTimeBitMon())
        .timeBitTue(entity.getTimeBitTue())
        .timeBitWed(entity.getTimeBitWed())
        .timeBitThu(entity.getTimeBitThu())
        .timeBitFri(entity.getTimeBitFri())
        .timeBitSat(entity.getTimeBitSat())
        .timeBitSun(entity.getTimeBitSun())
        .build();
  }

  // DTO → Entity
  public FavoriteTimetable toEntity(Member member) {
    return FavoriteTimetable.builder()
        .member(member)
        .timeBitMon(this.timeBitMon)
        .timeBitTue(this.timeBitTue)
        .timeBitWed(this.timeBitWed)
        .timeBitThu(this.timeBitThu)
        .timeBitFri(this.timeBitFri)
        .timeBitSat(this.timeBitSat)
        .timeBitSun(this.timeBitSun)
        .build();
  }
}
