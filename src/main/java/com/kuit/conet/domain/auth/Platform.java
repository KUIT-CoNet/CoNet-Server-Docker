package com.kuit.conet.domain.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kuit.conet.utils.PlatformDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = PlatformDeserializer.class)
public enum Platform {
    APPLE,
    KAKAO
}
