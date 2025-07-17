package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.group.dto.GroupSchedule;
import com.grepp.spring.app.model.group.dto.GroupUserDetail;
import com.grepp.spring.app.model.group.dto.WeekDetail;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowGroupStatisticsResponse {
    private Long scheduleNumber;
    private ArrayList<GroupUserDetail> groupUserDetails;
    private ArrayList<GroupSchedule> groupSchedules;
    private ArrayList<WeekDetail> weekDetails;

}
