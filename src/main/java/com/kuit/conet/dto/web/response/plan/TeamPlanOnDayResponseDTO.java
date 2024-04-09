package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.dto.plan.FixedPlanOnDayDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class TeamPlanOnDayResponseDTO {
    int count;
    List<FixedPlanOnDayDTO> plans;

    public TeamPlanOnDayResponseDTO(List<FixedPlanOnDayDTO> plans) {
        this.count = plans.size();
        this.plans = plans;
    }
}