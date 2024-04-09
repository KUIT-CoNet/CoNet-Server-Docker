package com.kuit.conet.dto.web.response.member;

import com.kuit.conet.domain.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StorageImgResponseDTO {
    private String name;
    private String imgUrl;

    public StorageImgResponseDTO(Team team) {
        this.name = team.getName();
        this.imgUrl = team.getImgUrl();
    }
}