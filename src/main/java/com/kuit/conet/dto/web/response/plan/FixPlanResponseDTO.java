package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.domain.plan.Plan;
import lombok.Getter;

import java.sql.Date;
import java.sql.Time;

@Getter
public class FixPlanResponseDTO {
    private String planName;
    private Date fixedDate;
    private Time fixedTime;
    private int memberCount;

    public FixPlanResponseDTO(Plan plan) {
        this.planName = plan.getName();
        this.fixedDate = plan.getFixedDate();
        this.fixedTime = plan.getFixedTime();
        this.memberCount = plan.getPlanMembersCount();
    }
}
