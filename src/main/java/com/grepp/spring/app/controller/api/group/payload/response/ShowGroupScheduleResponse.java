package com.grepp.spring.app.controller.api.group.payload;

import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowGroupScheduleResponse {
    private ArrayList<Long> scheduleIds;

}
