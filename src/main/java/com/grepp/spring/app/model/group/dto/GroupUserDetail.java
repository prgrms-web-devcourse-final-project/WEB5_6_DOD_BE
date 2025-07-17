package com.grepp.spring.app.model.group.dto;

import com.grepp.spring.app.model.group.code.GroupRole;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GroupUserDetail {
    private String userName;
    private Long scheduleNums;

}
