package com.kuit.conet.dto.web.response.auth;

import lombok.*;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String email;
    private String accessToken;
    private String refreshToken;
    private Boolean isRegistered;

}
