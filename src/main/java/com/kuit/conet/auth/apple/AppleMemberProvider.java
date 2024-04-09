package com.kuit.conet.auth.apple;

import com.kuit.conet.utils.auth.JwtParser;
import com.kuit.conet.utils.auth.PublicKeyGenerator;
import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.dto.web.response.auth.MemberResponseDTO;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_CLAIMS;

@Component
@RequiredArgsConstructor
public class AppleMemberProvider {
    private final JwtParser jwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public MemberResponseDTO getApplePlatformMember(String identityToken) {
        Map<String, String> headers = jwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = publicKeyGenerator.generateApplePublicKey(headers, applePublicKeys);

        Claims claims = jwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

        return new MemberResponseDTO(claims.getSubject(), claims.get("email", String.class));
        /*
           claims 의 subject = member domain 의 platformId
         * */
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new InvalidTokenException(INVALID_CLAIMS);
        }
    }
}
