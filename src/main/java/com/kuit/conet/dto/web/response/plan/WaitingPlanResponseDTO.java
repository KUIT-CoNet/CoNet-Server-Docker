package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.dto.plan.WaitingPlanDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class WaitingPlanResponseDTO {
    private int count;
    private List<WaitingPlanDTO> plans;

    public WaitingPlanResponseDTO(List<WaitingPlanDTO> plans) {
        this.count = plans.size();
        this.plans = plans;
    }
}