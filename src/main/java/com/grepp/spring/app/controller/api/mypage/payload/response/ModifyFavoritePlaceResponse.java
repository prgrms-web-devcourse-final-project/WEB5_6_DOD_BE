package com.grepp.spring.app.controller.api.mypage.payload.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyFavoritePlaceResponse {

  private List<ModifyFavLocationList> modifyFavLocations;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ModifyFavLocationList{
    private Long favoritePlaceId;
    private String stationName;
    private double latitude;
    private double longitude;
    private LocalDateTime updatedAt;
  }
}
