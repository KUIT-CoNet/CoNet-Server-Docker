package com.kuit.conet.dto.web.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDTO {
    private String platformId;
    private String email;
}