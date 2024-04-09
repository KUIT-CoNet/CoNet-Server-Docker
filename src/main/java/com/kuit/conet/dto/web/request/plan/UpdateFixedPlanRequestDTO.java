package com.kuit.conet.dto.web.request.plan;

import lombok.Getter;

import java.sql.Date;
import java.util.List;

@Getter
public class UpdateFixedPlanRequestDTO {
    private Long planId;
    private String planName;
    private Date date; // yyyy-MM-dd
    private String time; // HH:mm
    private List<Long> memberIds; // 구성원 Id 리스트
}