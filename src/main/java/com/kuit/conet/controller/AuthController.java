package com.kuit.conet.controller;

import com.kuit.conet.annotation.ClientIp;
import com.kuit.conet.annotation.RefreshToken;
import com.kuit.conet.annotation.MemberId;
import com.kuit.conet.common.exception.PlatformException;
import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.web.request.auth.LoginRequestDTO;
import com.kuit.conet.dto.web.request.auth.PutOptionTermAndNameRequestDTO;
import com.kuit.conet.dto.web.response.auth.TermAndNameResponseDTO;
import com.kuit.conet.dto.web.response.auth.LoginResponseDTO;
import com.kuit.conet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_PLATFORM;
import static com.kuit.conet.domain.auth.Platform.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * @apiNote 로그인 api
     * */
    @PostMapping("/login")
    public BaseResponse<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest, @ClientIp String clientIp) {
        if (loginRequest.getPlatform() == KAKAO) {
            LoginResponseDTO response = authService.kakaoLogin(loginRequest, clientIp);
            return new BaseResponse<>(response);
        }
        if (loginRequest.getPlatform() == APPLE) {
            LoginResponseDTO response = authService.appleLogin(loginRequest, clientIp);
            return new BaseResponse<>(response);
        }

        throw new PlatformException(INVALID_PLATFORM);
    }

    /**
     * @apiNote 이용 약관 동의 및 이름 입력 api
     * */
    @PostMapping("/term")
    public BaseResponse<TermAndNameResponseDTO> agreeTermAndPutName(@MemberId Long memberId, @RequestBody @Valid PutOptionTermAndNameRequestDTO authRequest) {
        TermAndNameResponseDTO response = authService.agreeTermAndPutName(memberId, authRequest);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 토큰 재발급 api
     * */
    @PostMapping("/regenerate-token")
    public BaseResponse<LoginResponseDTO> regenerateToken(@MemberId @Valid Long memberId, @RefreshToken @Valid String refreshToken, @ClientIp String clientIp) {
        LoginResponseDTO response = authService.regenerateToken(memberId, refreshToken, clientIp);
        return new BaseResponse<>(response);
    }

}