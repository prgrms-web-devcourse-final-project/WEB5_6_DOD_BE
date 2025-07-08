package com.grepp.spring.app.controller.api.mypage.payload;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyFavoritePlaceResponse {

  private List<ModifyFavLocationList> modifyFavLocations;

  @Getter @Setter
  public static class ModifyFavLocationList{
    private Long favoritePlaceId;
    private String stationName;
    private double latitude;
    private double longitude;
    private LocalDateTime updatedAt;
  }



}
