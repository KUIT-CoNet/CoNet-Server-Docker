package com.kuit.conet.jpa.service;


import com.kuit.conet.domain.plan.TeamFixedPlanOnDay;
import com.kuit.conet.dto.request.plan.CreatePlanRequest;
import com.kuit.conet.dto.request.plan.TeamFixedPlanOnDayRequest;
import com.kuit.conet.dto.response.plan.CreatePlanResponse;
import com.kuit.conet.dto.response.plan.TeamPlanOnDayResponse;
import com.kuit.conet.jpa.domain.plan.Plan;
import com.kuit.conet.jpa.domain.team.Team;
import com.kuit.conet.jpa.repository.PlanRepository;
import com.kuit.conet.jpa.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
    private final TeamRepository teamRepository;
    private final PlanRepository planRepository;

    public CreatePlanResponse createPlan(CreatePlanRequest request) {
        Date endDate = setEndDate(new java.sql.Date(request.getPlanStartDate().getTime())); // TODO: Date 관련 타입
        Team team = teamRepository.findById(request.getTeamId());

        Plan newPlan = Plan.builder()
                .team(team)
                .name(request.getPlanName())
                .startPeriod(request.getPlanStartDate())
                .endPeriod(endDate)
                .build();
        Long planId = planRepository.save(newPlan);

        return new CreatePlanResponse(planId);
    }

    private static Date setEndDate(Date startDate) {
        LocalDate endDate = startDate.toLocalDate().plusDays(6);
        return Date.valueOf(endDate);
    }

    public TeamPlanOnDayResponse getPlanOnDay(TeamFixedPlanOnDayRequest request) {
        List<TeamFixedPlanOnDay> fixedPlansOnDay = planRepository.getFixedPlansOnDay(request.getTeamId(), request.getSearchDate());

        return new TeamPlanOnDayResponse(fixedPlansOnDay.size(), fixedPlansOnDay);
    }

}