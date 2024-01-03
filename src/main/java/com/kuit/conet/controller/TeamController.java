package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.web.request.team.CreateTeamRequest;
import com.kuit.conet.dto.web.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.web.request.team.TeamIdRequest;
import com.kuit.conet.dto.web.response.team.CreateTeamResponse;
import com.kuit.conet.dto.web.response.team.GetTeamResponse;
import com.kuit.conet.dto.web.response.team.ParticipateTeamResponse;
import com.kuit.conet.jpa.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    /**
     * @apiNote 모임 생성 api
     * */
    @PostMapping("/create")
    public BaseResponse<CreateTeamResponse> createTeam(@RequestPart(value = "request") @Valid CreateTeamRequest teamRequest, HttpServletRequest httpRequest, @RequestParam(value = "file") MultipartFile file) {
        CreateTeamResponse response = teamService.createTeam(teamRequest, httpRequest, file);
        return new BaseResponse<CreateTeamResponse>(response);
    }

    /**
     * @apiNote 모임 참가 api
     * */
    @PostMapping("/participate")
    public BaseResponse<ParticipateTeamResponse> participateTeam(@RequestBody @Valid ParticipateTeamRequest teamRequest, HttpServletRequest httpRequest) {
        ParticipateTeamResponse response = teamService.participateTeam(teamRequest, httpRequest);
        return new BaseResponse<ParticipateTeamResponse>(response);
    }

    /**
     * @apiNote 모임 리스트 조회 api
     * */
    @GetMapping
    public BaseResponse<List<GetTeamResponse>> getTeam(HttpServletRequest httpRequest) {
        List<GetTeamResponse> responses = teamService.getTeam(httpRequest);
        return new BaseResponse<List<GetTeamResponse>>(responses);
    }

    /**
     * @apiNote 모임 탈퇴 api
     * */
    @PostMapping("/leave")
    public BaseResponse<String> leaveTeam(@RequestBody @Valid TeamIdRequest teamRequest, HttpServletRequest httpRequest) {
        String response = teamService.leaveTeam(teamRequest, httpRequest);
        return new BaseResponse<String>(response);
    }

    /*

    @PostMapping("/code")
    public BaseResponse<RegenerateCodeResponse> regenerateCode(@RequestBody @Valid TeamIdRequest request) {
        RegenerateCodeResponse response = teamService.regenerateCode(request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/delete")
    public BaseResponse<String> deleteTeam(@RequestBody @Valid TeamIdRequest request) {
        String response = teamService.deleteTeam(request);
        return new BaseResponse<String>(response);
    }

    @PostMapping("/update")
    public BaseResponse<StorageImgResponse> updateTeam(@RequestPart(value = "request") @Valid UpdateTeamRequest updateTeamRequest, @RequestParam(value = "file") MultipartFile file) {
        StorageImgResponse response = teamService.updateTeam(updateTeamRequest, file);
        return new BaseResponse<StorageImgResponse>(response);
    }

    @GetMapping("/members")
    public BaseResponse<List<GetTeamMemberResponse>> getTeamMembers(@ModelAttribute @Valid TeamIdRequest request) {
        List<GetTeamMemberResponse> response = teamService.getTeamMembers(request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/bookmark")
    public BaseResponse<String> bookmarkTeam(HttpServletRequest httpRequest, @RequestBody @Valid TeamIdRequest request) {
        teamService.bookmarkTeam(httpRequest, request);
        return new BaseResponse<>("모임을 즐겨찾기에 추가하였습니다.");
    }

    @PostMapping("/bookmark/delete")
    public BaseResponse<String> unBookmarkTeam(HttpServletRequest httpRequest, @RequestBody @Valid TeamIdRequest request) {
        teamService.unBookmarkTeam(httpRequest, request);
        return new BaseResponse<>("모임을 즐겨찾기에서 삭제하였습니다.");
    }

    @GetMapping("/detail")
    public BaseResponse<GetTeamResponse> getTeamDetail(HttpServletRequest httpRequest, @ModelAttribute @Valid TeamIdRequest request) {
        GetTeamResponse response = teamService.getTeamDetail(httpRequest, request);
        return new BaseResponse<>(response);
    }

    @GetMapping("/bookmark")
    public BaseResponse<List<GetTeamResponse>> getBookmark(HttpServletRequest httpRequest) {
        List<GetTeamResponse> responses = teamService.getBookmarks(httpRequest);
        return new BaseResponse<>(responses);
    }*/
}
