package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
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
public class MetroInfoDto {
    private String locationName;
    private VoteStatus voteStatus;
    private Double latitude;
    private Double longitude;
    private List<MetroTransferDto> metroTransfer;

    public static MetroInfoDto toDto(Location location, List<MetroTransfer> transfer) {

//        VoteStatus voteStatus;
//
//        if (lid != null && lid == location.getId()) {
//            voteStatus = VoteStatus.WINNER;
//        } else {
//            voteStatus = VoteStatus.DEFAULT;
//        }

        return MetroInfoDto.builder()
            .locationName(location.getName())
            .voteStatus(location.getStatus())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .metroTransfer(MetroTransferDto.toDto(transfer))
            .build();
    }

}
