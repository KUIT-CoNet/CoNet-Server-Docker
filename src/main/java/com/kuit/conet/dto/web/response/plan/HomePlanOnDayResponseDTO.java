package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.dto.home.HomeFixedPlanOnDayDTO;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomePlanOnDayResponseDTO {
    int count;
    List<HomeFixedPlanOnDayDTO> plans;
}