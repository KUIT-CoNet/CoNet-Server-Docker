package com.kuit.conet.dto.web.request.plan;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
public class FixPlanRequestDTO {
    private Long planId;
    private Date fixedDate;
    private Long fixedTime;
    private List<Long> memberIds;
}
