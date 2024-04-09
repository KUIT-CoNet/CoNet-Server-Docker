package com.kuit.conet.dto.web.response.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegenerateCodeResponseDTO {
    private Long teamId;
    private String inviteCode;
    private String codeDeadLine;
}
