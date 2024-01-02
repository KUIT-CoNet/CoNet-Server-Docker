package com.kuit.conet.jpa.service;

import com.kuit.conet.domain.plan.HomeFixedPlanOnDay;
import com.kuit.conet.domain.plan.WaitingPlan;
import com.kuit.conet.dto.web.request.plan.HomePlanRequest;
import com.kuit.conet.dto.web.response.plan.HomePlanOnDayResponse;
import com.kuit.conet.dto.web.response.plan.PlanDateOnMonthResponse;
import com.kuit.conet.dto.web.response.plan.WaitingPlanResponse;
import com.kuit.conet.jpa.repository.HomeRepository;
import com.kuit.conet.utils.DateFormatter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;

    public PlanDateOnMonthResponse getHomeFixedPlanInMonth(HttpServletRequest httpRequest, HomePlanRequest homeRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        List<Date> fixedPlansInMonth = homeRepository.getHomeFixedPlansInMonth(userId, homeRequest.getSearchDate());
        List<Integer> planDates = DateFormatter.datesToIntegerList(fixedPlansInMonth);

        return new PlanDateOnMonthResponse(planDates.size(), planDates);
    }

    public HomePlanOnDayResponse getHomeFixedPlanOnDay(HttpServletRequest httpRequest, HomePlanRequest homeRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        List<HomeFixedPlanOnDay> plans = homeRepository.getHomeFixedPlansOnDay(userId, homeRequest.getSearchDate());

        return new HomePlanOnDayResponse(plans.size(), plans);
    }

    public WaitingPlanResponse getHomeWaitingPlan(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        List<WaitingPlan> plans = homeRepository.getHomeWaitingPlans(userId);

        return new WaitingPlanResponse(plans.size(), plans);
    }

}
