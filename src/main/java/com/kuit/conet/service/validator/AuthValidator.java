package com.kuit.conet.service.validator;

import com.kuit.conet.common.exception.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
public class AuthValidator {
    public static void validateRefreshTokenExisting(String existingIp) {
        if (existingIp == null) {
            log.error(INVALID_REFRESH_TOKEN.getMessage());
            throw new InvalidTokenException(INVALID_REFRESH_TOKEN);
        }
    }

    public static void compareClientIpFromRedis(String existingIp, String clientIp) {
        if (!existingIp.equals(clientIp)) {
            log.error(IP_MISMATCH.getMessage());
            throw new InvalidTokenException(IP_MISMATCH);
        }
    }
}
