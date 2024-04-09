package com.kuit.conet.dto.web.request.auth;

import com.kuit.conet.domain.auth.Platform;
import lombok.Getter;

@Getter
public class LoginRequestDTO {
    private Platform platform;
    private String idToken;
}