package com.kuit.conet.dto.web.request.plan;

import com.kuit.conet.domain.plan.PlanPeriod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamFixedPlanInPeriodRequestDTO {
    private Long teamId;
    private PlanPeriod period;

}
