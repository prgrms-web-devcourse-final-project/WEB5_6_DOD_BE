package com.grepp.spring.app.model.mypage.converter;

import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FavoriteTimetableConverter {
  public static String toHex(Long timeBit) {
    return String.format("%012x", timeBit);
  }

  public static Map<String, String> toDayBitMap(List<FavoriteTimetableDto> dtos) {
      return dtos.stream().collect(Collectors.toMap(
        FavoriteTimetableDto::getDay,
        dto -> toHex(dto.getTimeBit()) // 10진수 -> 16진수로 바꾸기
    ));
  }

}
