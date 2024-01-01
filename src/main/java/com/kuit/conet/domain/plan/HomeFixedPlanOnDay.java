package com.kuit.conet.domain.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeFixedPlanOnDay {
    private Long planId;
    private Time time; // hh-mm
    private String teamName;
    private String planName;
}
/*
 * 날짜 / 시각 / 모임 명 / 약속 명
 * 디데이 추가
 *   - 시간 관계 없이 ‘오늘’을 기준으로 며칠 남았는지
 */