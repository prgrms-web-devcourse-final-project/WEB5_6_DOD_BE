package com.grepp.spring.app.controller.api.group.payload;

import com.grepp.spring.app.controller.api.group.groupDto.groupSchedule.GroupSchedule;
import com.grepp.spring.app.controller.api.group.groupDto.groupUser.GroupUserDetail;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowGroupStatisticsResponse {
    private ArrayList<GroupUserDetail> groupUserDetails;
    private ArrayList<GroupSchedule> groupSchedules;

}
