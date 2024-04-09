package com.kuit.conet.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberAvailableTimeDTO {
    private Long memberId;
    private String memberName;
    private String possibleTime;

}
