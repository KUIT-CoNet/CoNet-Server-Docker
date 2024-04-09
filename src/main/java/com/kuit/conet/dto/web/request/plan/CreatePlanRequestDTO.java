package com.kuit.conet.dto.web.request.plan;

import lombok.Getter;

import java.sql.Date;

@Getter
public class CreatePlanRequestDTO {
    private Long teamId;
    private String planName;
    private Date planStartDate;
}
