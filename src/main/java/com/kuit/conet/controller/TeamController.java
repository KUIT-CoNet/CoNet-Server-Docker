package com.kuit.conet.controller;

import com.kuit.conet.annotation.MemberId;
import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.web.request.team.CreateTeamRequestDTO;
import com.kuit.conet.dto.web.request.team.JoinTeamRequestDTO;
import com.kuit.conet.dto.web.request.team.TeamIdRequestDTO;
import com.kuit.conet.dto.web.request.team.UpdateTeamRequestDTO;
import com.kuit.conet.dto.web.response.member.StorageImgResponseDTO;
import com.kuit.conet.dto.web.response.team.*;
import com.kuit.conet.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    /**
     * @apiNote 모임 생성 api
     */
    @PostMapping
    public BaseResponse<CreateTeamResponseDTO> createTeam(@RequestPart(value = "request") @Valid CreateTeamRequestDTO teamRequest, @MemberId Long memberId, @RequestPart(value = "file") MultipartFile file) {
        CreateTeamResponseDTO response = teamService.createTeam(teamRequest, memberId, file);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 참가 api
     */
    @PostMapping("/join")
    public BaseResponse<JoinTeamResponseDTO> joinTeam(@RequestBody @Valid JoinTeamRequestDTO teamRequest, @MemberId Long memberId) {
        JoinTeamResponseDTO response = teamService.joinTeam(teamRequest, memberId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 리스트 조회 api
     */
    @GetMapping
    public BaseResponse<List<GetTeamResponseDTO>> getTeam(@MemberId Long memberId) {
        List<GetTeamResponseDTO> responses = teamService.getTeam(memberId);
        return new BaseResponse<>(responses);
    }

    /**
     * @apiNote 모임 탈퇴 api
     */
    @PostMapping("/leave")
    public BaseResponse<String> leaveTeam(@RequestBody @Valid TeamIdRequestDTO teamRequest, @MemberId Long memberId) {
        String response = teamService.leaveTeam(teamRequest, memberId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 삭제 api
     */
    @DeleteMapping("/{teamId}")
    public BaseResponse<String> deleteTeam(@PathVariable Long teamId, @MemberId Long memberId) {
        String response = teamService.deleteTeam(teamId, memberId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 구성원 조회 api
     */
    @GetMapping("/{teamId}/members")
    public BaseResponse<List<GetTeamMemberResponseDTO>> getTeamMembers(@PathVariable @Valid Long teamId, @MemberId @Valid Long memberId) {
        List<GetTeamMemberResponseDTO> response = teamService.getTeamMembers(teamId, memberId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 상세 조회 api
     */
    @GetMapping("/{teamId}")
    public BaseResponse<GetTeamResponseDTO> getTeamDetail(@PathVariable Long teamId, @MemberId Long memberId) {
        GetTeamResponseDTO response = teamService.getTeamDetail(teamId, memberId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 수정 api
     */
    @PostMapping("/update")
    public BaseResponse<StorageImgResponseDTO> updateTeam(@RequestPart(value = "request") @Valid UpdateTeamRequestDTO teamRequest, @MemberId Long memberId, @RequestParam(value = "file") MultipartFile file) {
        StorageImgResponseDTO response = teamService.updateTeam(teamRequest, memberId, file);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 코드 재발급 api
     */
    @PostMapping("/code")
    public BaseResponse<RegenerateCodeResponseDTO> regenerateCode(@RequestBody @Valid TeamIdRequestDTO teamRequest) {
        RegenerateCodeResponseDTO response = teamService.regenerateCode(teamRequest);
        return new BaseResponse<>(response);
    }
}
