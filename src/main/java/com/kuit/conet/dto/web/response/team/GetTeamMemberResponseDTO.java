package com.kuit.conet.dto.web.response.team;

import lombok.*;

@Getter
@AllArgsConstructor
public class GetTeamMemberResponseDTO {
    private Long memberId;
    private String name;
    private String memberImgUrl;
}
