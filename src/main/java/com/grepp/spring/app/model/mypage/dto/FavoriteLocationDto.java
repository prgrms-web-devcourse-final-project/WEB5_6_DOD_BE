package com.grepp.spring.app.model.mypage.dto;


import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoritePlaceResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoritePlaceResponse.FavoriteLocationList;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
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
public class FavoriteLocationDto {
  private Long favoriteLocationId;
  private String memberId;
  private String stationName;
  private Double latitude;
  private Double longitude;

  // Entity → DTO
  public static FavoriteLocationDto fromEntity(FavoriteLocation entity) {
    return FavoriteLocationDto.builder()
        .favoriteLocationId(entity.getId())
        .memberId(entity.getMember().getId())
        .stationName(entity.getName())          //Entity 내 name 필드 -> stationName 으로 쓰기
        .latitude(entity.getLatitude())
        .longitude(entity.getLongitude())
        .build();

  }

  // DTO → Entity : 컨트롤러에서 받은 dto DB 에 저장하기 위해
  public FavoriteLocation toEntity() {
    Member member = new Member();
    member.setId(this.memberId);

    return FavoriteLocation.builder()
        .id(this.favoriteLocationId)
        .member(member)
        .name(this.stationName)
        .latitude(this.latitude)
        .longitude(this.longitude)
        .build();
  }

  // dto -> response, 내부 리스트 아이템으로 response 로 변환해서 감쌈 -> controller 안에서 사용할 거
  public static CreateFavoritePlaceResponse fromDto(FavoriteLocationDto dto) {
    FavoriteLocationList item = new FavoriteLocationList();
    item.setFavoritePlaceId(dto.getFavoriteLocationId());
    item.setStationName(dto.getStationName());
    item.setLatitude(dto.getLatitude());
    item.setLongitude(dto.getLongitude());

    // 리스트 형태로 감싸서 응답 객체에 넣기 (요소 1개만 필요해서?, 수정 안해도 되니까?)
    return new CreateFavoritePlaceResponse(Collections.singletonList(item));
  }

  public static FavoriteLocationDto toDto(CreateFavoritePlaceRequest request, Member member) {
    return FavoriteLocationDto.builder()
        .memberId(member.getId())
        .stationName(request.getStationName())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .build();
  }



}
