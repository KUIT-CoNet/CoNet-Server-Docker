package com.kuit.conet.dto.web.response.team;

import com.kuit.conet.domain.team.Team;
import lombok.Getter;

@Getter
public class CreateTeamResponseDTO {
    private Long teamId;
    private String inviteCode;

    public CreateTeamResponseDTO(Team team) {
        this.teamId = team.getId();
        this.inviteCode = team.getInviteCode();
    }
}