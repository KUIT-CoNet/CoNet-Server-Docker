package com.kuit.conet.service;

import com.kuit.conet.common.exception.TeamException;
import com.kuit.conet.dao.TeamDao;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.domain.Team;
import com.kuit.conet.domain.TeamMember;
import com.kuit.conet.dto.request.team.MakeTeamRequest;
import com.kuit.conet.dto.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.response.team.MakeTeamResponse;
import com.kuit.conet.dto.response.team.ParticipateTeamResponse;
import com.kuit.conet.utils.JwtParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamDao teamDao;
    private final UserDao userDao;
    private final JwtParser jwtParser;

    public MakeTeamResponse makeTeam(MakeTeamRequest request) {
        // 초대 코드 생성
        String inviteCode;

        // 코드 중복 확인
        do {
            inviteCode = generateInviteCode();
        } while(teamDao.validateDuplicateCode(inviteCode));  // 중복되면 true 반환

        // 모임 생성 시간 찍기
        Timestamp codeGeneratedTime = Timestamp.valueOf(LocalDateTime.now());

        // team table에 새로운 team insert하고 teamId 얻기
        Team newTeam = new Team(request.getTeamName(), request.getTeamImgUrl(), inviteCode, codeGeneratedTime);
        Long teamId = teamDao.saveTeam(newTeam);

        // teamMembers 에 userId 추가
        // TODO: teamMemberId 가져올 필요 X
        TeamMember newTeamMember = new TeamMember(teamId, request.getUserId());
        TeamMember savedTeamMember = teamDao.saveTeamMember(newTeamMember);

        return new MakeTeamResponse(teamId, inviteCode, codeGeneratedTime);
    }

    public String generateInviteCode() {
        int leftLimit = 48;
        int rigntLimit = 122;
        int targetStirngLength = 8;

        Random random = new Random();

        String generatedString = random.ints(leftLimit, rigntLimit+1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStirngLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public ParticipateTeamResponse participateTeam(ParticipateTeamRequest participateRequest) {
        // 모임 참가 요청 시간 찍기
        LocalDateTime participateRequestTime = LocalDateTime.now();

        // 초대 코드 존재 확인
        if (!teamDao.validateDuplicateCode(participateRequest.getInviteCode())) {
            throw new TeamException(NOT_FOUND_INVITE_CODE);
        }

        String userId = jwtParser.getUserIdFromToken(participateRequest.getToken());
        participateRequest.setToken(userId);

        String userName = userDao.findById(Long.parseLong(participateRequest.getToken())).get().getName();

        Team team = teamDao.getTeamFromInviteCode(participateRequest);

        // 모임에 이미 존재하는 회원인지 확인
        if (teamDao.isExistingUser(team.getTeamId(), participateRequest)) {
            throw new TeamException(EXIST_USER_IN_TEAM);
        }

        // 초대 코드 생성 시간과 모임 참가 요청 시간 비교
        LocalDateTime generatedTime = team.getCodeGeneratedTime().toLocalDateTime();
        LocalDateTime expirationDateTime = generatedTime.plusDays(1);

        log.info("generatedTime: {}", generatedTime);
        log.info("expirationDateTime: {}", expirationDateTime);
        log.info("participateRequestTime: {}", participateRequestTime);

        if (participateRequestTime.isAfter(expirationDateTime)) {
            // 초대 코드 생성 시간으로부터 1일이 지났으면 exception
            log.info("유효 기간 만료: {}", EXPIRED_INVITE_CODE.getMessage());
            throw new TeamException(EXPIRED_INVITE_CODE);
        }

        // teamMember 에 userId 추가
        TeamMember newTeamMember = new TeamMember(team.getTeamId(), Long.parseLong(participateRequest.getToken()));
        TeamMember savedTeamMember = teamDao.saveTeamMember(newTeamMember);

        return new ParticipateTeamResponse(userName, team.getTeamName(), savedTeamMember.getStatus());
    }
}
