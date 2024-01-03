package com.kuit.conet.jpa.service;

import com.kuit.conet.common.exception.TeamException;
import com.kuit.conet.dto.web.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.web.request.team.TeamIdRequest;
import com.kuit.conet.dto.web.response.team.CreateTeamResponse;
import com.kuit.conet.dto.web.response.team.GetTeamResponse;
import com.kuit.conet.dto.web.response.team.ParticipateTeamResponse;
import com.kuit.conet.jpa.domain.member.Member;
import com.kuit.conet.jpa.domain.team.TeamMember;
import com.kuit.conet.jpa.domain.team.*;

import com.kuit.conet.domain.storage.StorageDomain;
import com.kuit.conet.dto.web.request.team.CreateTeamRequest;
import com.kuit.conet.jpa.repository.TeamMemberRepository;
import com.kuit.conet.jpa.repository.TeamRepository;
import com.kuit.conet.jpa.repository.UserRepository;
import com.kuit.conet.jpa.service.validator.TeamValidator;
import com.kuit.conet.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {
    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    public CreateTeamResponse createTeam(CreateTeamRequest teamRequest, HttpServletRequest httpRequest, MultipartFile file) {
        // 초대 코드 생성 및 코드 중복 확인
        String inviteCode = getInviteCode();

        // 모임 생성 시간 찍기
        LocalDateTime codeGeneratedTime = LocalDateTime.now();

        // 팀 만든 멤버 정보 추출
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        Member teamCreator = userRepository.findById(userId);

        //이미지 s3 업로드
        String imgUrl = updateTeamImg(file);

        // team 생성
        Team newTeam = Team.createTeam(teamRequest.getTeamName(), inviteCode,codeGeneratedTime,teamCreator,imgUrl);
        teamRepository.save(newTeam);

        return new CreateTeamResponse(newTeam.getId(), newTeam.getInviteCode());
    }

    private String getInviteCode() {
        String inviteCode = generateInviteCode();
        while(true){
            if(TeamValidator.validateDuplicateInviteCode(teamRepository, inviteCode))
                break;
            inviteCode = generateInviteCode();
        }
        return inviteCode;
    }

    private String updateTeamImg(MultipartFile file) {
        // 새로운 이미지 S3에 업로드
        String fileName = storageService.getFileName(file, StorageDomain.TEAM);
        String imgUrl = storageService.uploadToS3(file, fileName);
        return imgUrl;
    }

    public String generateInviteCode() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 8;

        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit+1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public ParticipateTeamResponse participateTeam(ParticipateTeamRequest teamRequest, HttpServletRequest httpRequest) {
        // 모임 참가 요청 시간 찍기
        LocalDateTime participateRequestTime = LocalDateTime.now();

        // 초대 코드 존재 확인
        //todo method 추출하기
        String inviteCode = teamRequest.getInviteCode();
        if (!teamRepository.isExistInviteCode(inviteCode)) {
            throw new TeamException(NOT_FOUND_INVITE_CODE);
        }

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        Member user = userRepository.findById(userId);

        Team team = teamRepository.findByInviteCode(inviteCode);

        // 모임에 이미 존재하는 회원인지 확인
        if (teamRepository.isExistUser(team.getId(), userId)) {
            throw new TeamException(EXIST_USER_IN_TEAM);
        }

        // 초대 코드 생성 시간과 모임 참가 요청 시간 비교
        LocalDateTime generatedTime = team.getCodeGeneratedTime();
        LocalDateTime expirationDateTime = generatedTime.plusDays(1);

        //log.info("Team invite code generated time: {}", generatedTime);
        log.info("Team invite code expiration date time: {}", expirationDateTime);
        log.info("Team participation requested time: {}", participateRequestTime);

        if (participateRequestTime.isAfter(expirationDateTime)) {
            // 초대 코드 생성 시간으로부터 1일이 지났으면 exception
            log.error("유효 기간 만료: {}", EXPIRED_INVITE_CODE.getMessage());
            throw new TeamException(EXPIRED_INVITE_CODE);
        }

        // team에 teamMember 추가 (변경 감지)
        TeamMember teamMember = TeamMember.createTeamMember(team, user);
        team.addTeamMember(teamMember);

        return new ParticipateTeamResponse(user.getName(), team.getName(), user.getStatus());
    }

    public List<GetTeamResponse> getTeam(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        List<Team> teams = teamRepository.findByUserId(userId);
        List<GetTeamResponse> teamReturnResponses = new ArrayList<>();

        // 모임의 created_at 시간 비교해서 3일 안지났으면 isNew 값 true, 지났으면 false로 반환
        for(Team team : teams) {
            log.info("{}",  team.getName());
            LocalDateTime createdAt = team.getCreatedAt();
            LocalDateTime now = LocalDateTime.now();

            GetTeamResponse teamResponse;
            if(now.minusDays(3).isAfter(createdAt)) {
                teamResponse = new GetTeamResponse(team.getId(), team.getName(), team.getImgUrl(), teamRepository.getMemberCount(team.getId()),
                        false, teamMemberRepository.isBookmark(userId, team.getId()));
            }else {
                teamResponse = new GetTeamResponse(team.getId(), team.getName(), team.getImgUrl(), teamRepository.getMemberCount(team.getId()),
                        true, teamMemberRepository.isBookmark(userId, team.getId()));
            }
            teamReturnResponses.add(teamResponse);
        }

        return teamReturnResponses;
    }

    public String leaveTeam(TeamIdRequest teamRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        Team team = teamRepository.findById(teamRequest.getTeamId());

        // 모임 존재 여부 확인
        if (team==null) {
            throw new TeamException(NOT_FOUND_TEAM);
        }

        //변경 감지 이용
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(team.getId(), userId);
        team.deleteMember(teamMember);

        return "모임 탈퇴에 성공하였습니다.";
    }

    //TODO: com.kuit.conet.service.TeamService 에 주석 처리된 코드 참고
    //      -> 해당 메서드에 대하여 JPA 변환 완료하면 com.kuit.conet.service.TeamService에서 (주석)코드 삭제하기

}
