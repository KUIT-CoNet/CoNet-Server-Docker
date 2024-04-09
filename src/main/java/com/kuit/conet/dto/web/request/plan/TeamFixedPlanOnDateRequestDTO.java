package com.kuit.conet.dto.web.request.plan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamFixedPlanOnDateRequestDTO {
    private Long teamId;
    private String searchDate;
    // 특정 날짜 조회: "yyyy-MM-dd"
    // 특정 달 조회: "yyyy-MM"
}