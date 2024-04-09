package com.kuit.conet.dto.web.response.plan;

import lombok.*;

import java.util.List;

@Getter
public class PlanDateOnMonthResponseDTO {
    int count;
    List<Integer> dates;

    public PlanDateOnMonthResponseDTO(List<Integer> dates) {
        this.count = dates.size();
        this.dates = dates;
    }
}
