package com.kuit.conet.controller;

import com.kuit.conet.annotation.MemberId;
import com.kuit.conet.common.exception.PlanException;
import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.web.request.plan.TeamFixedPlanOnDateRequestDTO;
import com.kuit.conet.dto.web.request.plan.TeamWaitingPlanRequestDTO;
import com.kuit.conet.dto.web.response.plan.PlanDetailResponseDTO;
import com.kuit.conet.dto.web.request.plan.TeamFixedPlanInPeriodRequestDTO;
import com.kuit.conet.dto.web.request.plan.*;
import com.kuit.conet.dto.web.response.plan.*;
import com.kuit.conet.domain.plan.PlanPeriod;
import com.kuit.conet.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.BAD_REQUEST;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanService planService;

    /**
     * @apiNote 약속 생성 api
     * */
    @PostMapping
    public BaseResponse<CreatePlanResponseDTO> createPlan(@MemberId @Valid Long memberId, @RequestBody @Valid CreatePlanRequestDTO planRequest) {
        CreatePlanResponseDTO response = planService.createPlan(memberId, planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 약속 상세 정보 조회 api / 확정 약속만 가능
     */
    @GetMapping("/{planId}")
    public BaseResponse<PlanDetailResponseDTO> getPlan(@PathVariable @Valid Long planId) {
        PlanDetailResponseDTO response = planService.getPlanDetail(planId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 내 특정 날짜 확정 약속 조회 api
     * / '나'의 직접적인 참여 여부와 무관
     * */
    @GetMapping("/day")
    public BaseResponse<TeamPlanOnDayResponseDTO> getFixedPlanOnDay(@ModelAttribute @Valid TeamFixedPlanOnDateRequestDTO planRequest) {
        TeamPlanOnDayResponseDTO response = planService.getFixedPlanOnDay(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 모임 내 특정 달의 약속이 존재하는 날짜 조회 api
     */
    @GetMapping("/month")
    public BaseResponse<PlanDateOnMonthResponseDTO> getFixedPlanInMonth(@ModelAttribute @Valid TeamFixedPlanOnDateRequestDTO planRequest) {
        PlanDateOnMonthResponseDTO response = planService.getFixedPlanInMonth(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote [사이드바 > 대기중인 약속] 모임 내 대기 중인 약속 조회 api
     * / '나'의 직접적인 참여 여부와 무관
     */
    @GetMapping("/waiting")
    public BaseResponse<WaitingPlanResponseDTO> getWaitingPlan(@ModelAttribute @Valid TeamWaitingPlanRequestDTO planRequest) {
        WaitingPlanResponseDTO response = planService.getTeamWaitingPlan(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote [사이드바 > 내 약속] 모임 내 확정된 (지난/다가오는) 약속 조회 api
     * / 조회한 유저의 참여 여부 포함
     */
    @GetMapping("/fixed")
    public BaseResponse<SideMenuFixedPlanResponseDTO> getFixedPlan(@MemberId @Valid Long memberId, @ModelAttribute TeamFixedPlanInPeriodRequestDTO planRequest) {
        // 지난 약속
        if (planRequest.getPeriod() == PlanPeriod.PAST) {
            SideMenuFixedPlanResponseDTO response = planService.getFixedPastPlan(memberId, planRequest.getTeamId());
            return new BaseResponse<>(response);
        }

        // 다가오는 약속
        if (planRequest.getPeriod() == PlanPeriod.ONCOMING) {
            SideMenuFixedPlanResponseDTO response = planService.getFixedOncomingPlan(memberId, planRequest.getTeamId());
            return new BaseResponse<>(response);
        }

        throw new PlanException(BAD_REQUEST);
    }

    /**
     * @apiNote 약속 확정 api
     */
    @PostMapping("/fix")
    public BaseResponse<FixPlanResponseDTO> fixPlan(@MemberId @Valid Long memberId, @RequestBody @Valid FixPlanRequestDTO planRequest) {
        FixPlanResponseDTO response = planService.fixPlan(memberId, planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 대기 중인 특정 약속의 구성원, 가능한 모든 시간 조회 api
     */
    @GetMapping("/{planId}/available-time-slot")
    public BaseResponse<AllMemberAvailableTimeResponseDTO> getAllMemberTimeSlot(@PathVariable @Valid Long planId) {
        AllMemberAvailableTimeResponseDTO response = planService.getAllMemberAvailableTimeSlot(planId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 대기 중인 특정 약속의 나의 가능한 시간 조회 api
     */
    @GetMapping("/{planId}/available-time-slot/my")
    public BaseResponse<OneMemberAvailableTimeResponseDTO> getOneMemberTimeSlot(@PathVariable @Valid Long planId, @MemberId @Valid Long memberId) {
        OneMemberAvailableTimeResponseDTO response = planService.getOneMemberAvailableTimeSlot(planId, memberId);
        return new BaseResponse<>(response);
    }

    /**
     * @apiNote 대기 중인 특정 약속의 나의 가능한 시간 저장 api
     */
    @PostMapping("/available-time-slot")
    public BaseResponse<String> registerAvailableTime(@MemberId @Valid Long memberId, @RequestBody @Valid RegisterAvailableTimeRequestDTO planRequest) {
        planService.registerAvailableTime(memberId, planRequest);
        return new BaseResponse<>("사용자의 가능한 시간 등록에 성공하였습니다.");
    }

    /**
     * @apiNote 확정된 약속 수정 api
     */
    @PostMapping("/update/fixed")
    public BaseResponse<String> updateFixedPlan(@MemberId @Valid Long memberId, @RequestBody @Valid UpdateFixedPlanRequestDTO planRequest) {
        planService.updateFixedPlan(memberId, planRequest);
        return new BaseResponse<>("확정 약속의 정보 수정을 성공하였습니다.");
    }

    /**
     * @apiNote 대기 중인 약속 수정 api
     */
    @PostMapping("/update/waiting")
    public BaseResponse<String> updateWaitingPlan(@MemberId @Valid Long memberId, @RequestBody @Valid UpdateWaitingPlanRequestDTO planRequest) {
        planService.updateWaitingPlan(memberId, planRequest);
        return new BaseResponse<>("대기 중인 약속의 정보 수정을 성공하였습니다.");
    }

    /**
     * @apiNote 약속 삭제 api
     */
    @DeleteMapping("/{planId}")
    public BaseResponse<String> deletePlan(@MemberId @Valid Long memberId, @PathVariable @Valid Long planId) {
        planService.deletePlan(memberId, planId);
        return new BaseResponse<>("약속 삭제에 성공하였습니다.");
    }

    /**
     * @apiNote [확정 약속 수정] 모임 구성원 약속 참여 여부 조회 api
     */
    @GetMapping("/{planId}/member-participation")
    public BaseResponse<List<MemberIsInResponseDTO>> getAllMemberParticipationOnPlan(@PathVariable @Valid Long planId) {
        List<MemberIsInResponseDTO> response = planService.getAllMemberIsInPlan(planId);
        return new BaseResponse<>(response);
    }
}