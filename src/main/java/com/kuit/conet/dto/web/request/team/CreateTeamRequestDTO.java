package com.kuit.conet.dto.web.request.team;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateTeamRequestDTO {
    @NotNull
    private String teamName;
}