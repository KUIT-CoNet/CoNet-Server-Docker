package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.dto.plan.PlanMemberDTO;
import com.kuit.conet.domain.plan.Plan;
import lombok.*;

import java.util.List;

import static com.kuit.conet.utils.DateAndTimeFormatter.*;

@Getter
public class PlanDetailResponseDTO {
    private Long planId;
    private String planName;
    private String date; // yyyy. MM. dd
    private String time; // HH:mm

    // 참여자 정보
    private int memberCount;
    private List<PlanMemberDTO> members;

    public PlanDetailResponseDTO(Plan plan, List<PlanMemberDTO> members) {
        this.planId = plan.getId();
        this.planName = plan.getName();
        this.date = dateToStringWithDot(plan.getFixedDate());
        this.time = timeDeleteSeconds(plan.getFixedTime());
        this.memberCount = members.size();
        this.members = members;
    }
}
