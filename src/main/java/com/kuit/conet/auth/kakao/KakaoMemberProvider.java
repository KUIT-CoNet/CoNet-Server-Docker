package com.kuit.conet.auth.kakao;

import com.kuit.conet.dto.web.response.auth.MemberResponseDTO;
import com.kuit.conet.utils.auth.JwtParser;
import com.kuit.conet.utils.auth.PublicKeyGenerator;
import com.kuit.conet.common.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_CLAIMS;

@Component
@RequiredArgsConstructor
public class KakaoMemberProvider {
    private final JwtParser jwtParser;
    private final PublicKeyGenerator publicKeyGenerator;

    private final KakaoClient kakaoClient;
    @Value("${oauth.kakao.iss}")
    private String iss;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    public MemberResponseDTO getPayloadFromIdToken(String identityToken) {
        Map<String, String> headers = jwtParser.parseHeaders(identityToken);
        KakaoPublicKeys kakaoPublicKeys = kakaoClient.getKakaoOIDCOpenKeys();
        PublicKey publicKey = publicKeyGenerator.generateKakaoPublicKey(headers, kakaoPublicKeys);

        Claims claims = jwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

        return new MemberResponseDTO(claims.getSubject(), claims.get("email", String.class));
    }
    private void validateClaims(Claims claims) {
        if (!claims.getIssuer().contains(iss) && claims.getAudience().equals(clientId)) {
            throw new InvalidTokenException(INVALID_CLAIMS);
        }
    }
}