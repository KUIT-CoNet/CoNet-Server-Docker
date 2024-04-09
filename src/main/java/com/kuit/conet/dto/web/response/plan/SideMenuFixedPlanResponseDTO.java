package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.dto.plan.SideMenuFixedPlanDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class SideMenuFixedPlanResponseDTO {
    int count;
    List<SideMenuFixedPlanDTO> plans;

    public SideMenuFixedPlanResponseDTO(List<SideMenuFixedPlanDTO> plans) {
        this.count = plans.size();
        this.plans = plans;
    }
}