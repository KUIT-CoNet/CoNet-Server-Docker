package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.domain.plan.Plan;
import com.kuit.conet.dto.plan.MemberDateTimeDTO;
import com.kuit.conet.utils.DateAndTimeFormatter;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class AllMemberAvailableTimeResponseDTO {
    private Long teamId;
    private Long planId;
    private String planName;
    private String planStartPeriod;
    private String planEndPeriod;
    private Map<String, Long> endNumberForEachSection; // 각 구간의 최대 인원 수
    private List<MemberDateTimeDTO> availableMemberDateTime;

    public AllMemberAvailableTimeResponseDTO(Long teamId, Plan plan, Map<String, Long> endNumberForEachSection, List<MemberDateTimeDTO> availableMemberDateTime) {
        this.teamId = teamId;
        this.planId = plan.getId();
        this.planName = plan.getName();
        this.planStartPeriod = DateAndTimeFormatter.dateToStringWithDot(plan.getStartPeriod());
        this.planEndPeriod = DateAndTimeFormatter.dateToStringWithDot(plan.getEndPeriod());
        this.endNumberForEachSection = endNumberForEachSection;
        this.availableMemberDateTime = availableMemberDateTime;
    }
}
