package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberIsInResponseDTO {
    private Long memberId;
    private String name;
    private String memberImgUrl;
    private boolean isInPlan;

    public MemberIsInResponseDTO(Member member, boolean isInPlan) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.memberImgUrl = member.getImgUrl();
        this.isInPlan = isInPlan;
    }

}
