package com.grepp.spring.app.model.mypage.dto;


import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoriteTimeRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse.FavoriteTimeList;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
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
  private String memberId;
  private LocalTime startTime;
  private LocalTime endTime;
  private DayOfWeek weekday;


  // Entity → DTO
  public static FavoriteTimetableDto fromEntity(FavoriteTimetable entity) {
    return FavoriteTimetableDto.builder()
        .favoriteTimetableId(entity.getId())
        .memberId(entity.getMember().getId())
        .startTime(entity.getStartTime())
        .endTime(entity.getEndTime())
        .weekday(entity.getWeekday())
        .build();
  }

  // DTO → Entity
  public static FavoriteTimetable toEntity(FavoriteTimetableDto dto) {
    Member member = new Member();
    member.setId(dto.getMemberId());

    return FavoriteTimetable.builder()
        .id(dto.getFavoriteTimetableId())
        .member(member)
        .startTime(dto.getStartTime())
        .endTime(dto.getEndTime())
        .weekday(dto.getWeekday())
        .build();
  }

  // dto -> response
  public static CreateFavoriteTimeResponse fromDto(FavoriteTimetableDto dto) {
    FavoriteTimeList item = new FavoriteTimeList();
    item.setFavoriteTimeId(dto.getFavoriteTimetableId());
    item.setStartTime(dto.getStartTime());
    item.setEndTime(dto.getEndTime());
    item.setWeekday(dto.getWeekday());

    return new CreateFavoriteTimeResponse(Collections.singletonList(item));
  }

  public static FavoriteTimetableDto toDto(CreateFavoriteTimeRequest request, Member member){
    return FavoriteTimetableDto.builder()
        .memberId(member.getId())
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .weekday(request.getWeekday())
        .build();
  }





}
