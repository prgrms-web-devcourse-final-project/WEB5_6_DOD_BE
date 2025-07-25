package com.grepp.spring.app.model.mypage.dto;


import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoriteTimeRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteTimetableDto {
  private Long favoriteTimetableId;
  private String memberId;
  private String day;
  private Long timeBit;

  // Entity → DTO
  public static FavoriteTimetableDto fromEntity(FavoriteTimetable entity) {
    return FavoriteTimetableDto.builder()
        .favoriteTimetableId(entity.getId())
        .memberId(entity.getMember().getId())
        .day(entity.getDay())
        .timeBit(entity.getTimeBit())
        .build();
  }

  // DTO → Entity
  public static FavoriteTimetable toEntity(FavoriteTimetableDto dto, Member member) {
    return FavoriteTimetable.builder()
        .id(dto.getFavoriteTimetableId())
        .member(member)
        .day(dto.getDay())
        .timeBit(dto.getTimeBit())
        .build();
  }

  // Response 변환 (요일별 맵으로)
  public static CreateFavoriteTimeResponse fromDto(Map<String, String> dayToBitMap) {
    return new CreateFavoriteTimeResponse(
        dayToBitMap.get("MON"),
        dayToBitMap.get("TUE"),
        dayToBitMap.get("WED"),
        dayToBitMap.get("THU"),
        dayToBitMap.get("FRI"),
        dayToBitMap.get("SAT"),
        dayToBitMap.get("SUN")
    );
  }

}
