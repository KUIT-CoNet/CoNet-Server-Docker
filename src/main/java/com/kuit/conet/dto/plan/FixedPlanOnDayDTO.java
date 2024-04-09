package com.kuit.conet.dto.plan;

import lombok.Getter;

import java.sql.Time;

import static com.kuit.conet.utils.DateAndTimeFormatter.*;

@Getter
public class FixedPlanOnDayDTO {
    private Long planId;
    private String planName;
    private String time; // HH:mm

    public FixedPlanOnDayDTO(Long planId, String planName, Time time) {
        this.planId = planId;
        this.planName = planName;
        this.time = timeDeleteSeconds(time);
    }
}