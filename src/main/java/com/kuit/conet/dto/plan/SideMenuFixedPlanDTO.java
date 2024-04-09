package com.kuit.conet.dto.plan;

import lombok.Getter;

import java.sql.Date;
import java.sql.Time;

import static com.kuit.conet.utils.DateAndTimeFormatter.*;

@Getter
public class SideMenuFixedPlanDTO {
    private Long planId;
    private String planName;
    private String date; // yyyy. MM. dd
    private String time; // HH:mm
    private Long dDay;
    private boolean isParticipant;

    //다가오는 약속
    public SideMenuFixedPlanDTO(Long planId, String planName, Date date, Time time, int dDay, boolean isParticipant) {
        this.planId = planId;
        this.planName = planName;
        this.date = dateToStringWithDot(date);
        this.time = timeDeleteSeconds(time);
        this.dDay = Long.valueOf(dDay);
        this.isParticipant = isParticipant;
    }
}