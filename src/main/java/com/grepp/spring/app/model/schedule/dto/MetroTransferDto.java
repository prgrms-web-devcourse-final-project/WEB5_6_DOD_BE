package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetroTransferDto {
    private String line;
    private String color;

    public static List<MetroTransferDto> toDto (List<MetroTransfer> metroTransfer) {

        List<MetroTransferDto> result = new ArrayList<>();

        for (MetroTransfer mt : metroTransfer) {
            result.add(MetroTransferDto.builder()
                    .line(mt.getLineName())
                    .color(mt.getColor())
                    .build());
        }

        return result;
    }
}
