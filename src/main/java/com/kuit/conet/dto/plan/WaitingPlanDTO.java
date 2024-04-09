package com.kuit.conet.dto.plan;

import lombok.Getter;

import java.sql.Date;

import static com.kuit.conet.utils.DateAndTimeFormatter.*;

@Getter
public class WaitingPlanDTO {
    private Long planId;
    private String startDate; // yyyy. MM. dd
    private String endDate; // yyyy. MM. dd
    private String teamName;
    private String planName;

    public WaitingPlanDTO(Long planId, Date startDate, Date endDate, String teamName, String planName) {
        this.planId = planId;
        this.startDate = dateToStringWithDot(startDate);
        this.endDate = dateToStringWithDot(endDate);
        this.teamName = teamName;
        this.planName = planName;
    }
}