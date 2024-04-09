package com.kuit.conet.service;


import com.kuit.conet.domain.member.Member;
import com.kuit.conet.domain.plan.Plan;
import com.kuit.conet.domain.plan.PlanMember;
import com.kuit.conet.domain.plan.PlanMemberTime;
import com.kuit.conet.domain.team.Team;
import com.kuit.conet.domain.team.TeamMember;
import com.kuit.conet.dto.plan.*;
import com.kuit.conet.dto.web.request.plan.*;
import com.kuit.conet.dto.web.response.plan.*;
import com.kuit.conet.repository.*;
import com.kuit.conet.utils.DateAndTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kuit.conet.service.validator.MemberValidator.validateMemberExisting;
import static com.kuit.conet.service.validator.PlanValidator.*;
import static com.kuit.conet.service.validator.TeamValidator.validateMemberIsTeamMember;
import static com.kuit.conet.utils.DateAndTimeFormatter.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
    private final static int ONE_WEEK_DAYS = 7;
    private final static int ONE_DAY_HOURS = 24;
    private final static int MIN_TIME_NUMBER = 0;
    private final static int MAX_TIME_NUMBER = 23;
    private final static String AVAILABLE_TIME_SPLIT_REGEX = ",";
    private final static String NO_AVAILABLE_TIME = "";
    private final static int SECTION_DIVISOR = 3;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PlanRepository planRepository;
    private final PlanMemberRepository planMemberRepository;
    private final PlanMemberTimeRepository planMemberTimeRepository;

    private static Date setEndDate(Date startDate) {
        LocalDate endDate = startDate.toLocalDate().plusDays(6);
        return Date.valueOf(endDate);
    }

    private static List<PlanMemberDTO> setPlanMemberDTOS(Plan plan) {
        return plan.getPlanMembers().stream()
                .map(planMember ->
                        new PlanMemberDTO(
                                planMember.getMember().getId(),
                                planMember.getMember().getName(),
                                planMember.getMember().getImgUrl()))
                .sorted(Comparator.comparing(PlanMemberDTO::getName))
                .toList();
    }

    private static List<OneMemberAvailableTimeDTO> getAllMemberAvailableTimeSlot(List<PlanMemberTime> planMemberTimes) {
        return planMemberTimes.stream()
                .map(planMemberTime -> {
                    OneMemberAvailableTimeDTO responseDTO = new OneMemberAvailableTimeDTO(planMemberTime.getDate());
                    String availableTime = planMemberTime.getAvailableTime();

                    List<Integer> timeList = new ArrayList<>();
                    if (!availableTime.isEmpty()) {
                        timeList = timeStringToIntegerList(availableTime);
                    }
                    responseDTO.setAvailableTimes(timeList);

                    return responseDTO;
                }).toList();
    }

    private static Map<String, Long> getIntervalEndBySection(Long totalMemberCount) {
        Map<String, Long> endNumberForEachSection = new HashMap<>();

        //기본 구간 크기와 나머지 계산
        Long baseIntervalSize = totalMemberCount / SECTION_DIVISOR;
        Long remainder = totalMemberCount % SECTION_DIVISOR;

        //3구간의 범위가 너무 크지 않게, 비교적 균등하게 범위가 나뉘도록 하기 위하여
        Long firstIntervalEnd = baseIntervalSize + (remainder > 0 ? 1 : 0);
        Long secondIntervalEnd = firstIntervalEnd + baseIntervalSize + (remainder > 1 ? 1 : 0);
        endNumberForEachSection.put("section1", firstIntervalEnd);
        endNumberForEachSection.put("section2", secondIntervalEnd);
        endNumberForEachSection.put("section3", totalMemberCount);

        return endNumberForEachSection;
    }

    private static int[] setSectionForEachTime(Map<String, Long> endNumberForEachSection, int[] membersCount) {
        int[] section = new int[ONE_DAY_HOURS];

        for (int time = MIN_TIME_NUMBER; time <= MAX_TIME_NUMBER; time++) {
            int count = membersCount[time];

            if (count <= endNumberForEachSection.get("section3")) section[time] = 3;
            if (count <= endNumberForEachSection.get("section2")) section[time] = 2;
            if (count <= endNumberForEachSection.get("section1")) section[time] = 1;
            if (count == 0) section[time] = 0;
        }

        return section;
    }

    public CreatePlanResponseDTO createPlan(Long memberId, CreatePlanRequestDTO planRequest) {
        Member member = memberRepository.findById(memberId);
        validateMemberExisting(member);
        validateMemberIsTeamMember(teamMemberRepository, planRequest.getTeamId(), memberId);

        Date endDate = setEndDate(planRequest.getPlanStartDate());
        Team team = teamRepository.findById(planRequest.getTeamId());

        Plan newPlan = Plan.createPlan(team, planRequest.getPlanName(), planRequest.getPlanStartDate(), endDate);
        Long planId = planRepository.save(newPlan);

        return new CreatePlanResponseDTO(planId);
    }

    public PlanDetailResponseDTO getPlanDetail(Long planId) {
        Plan plan = planRepository.findById(planId);

        //확정 약속인지 검사
        validatePlanIsFixed(plan);

        Plan planWithMembers = planRepository.findWithMembersById(planId);

        List<PlanMemberDTO> planMemberList = new ArrayList<>();
        if (!isPlanMemberEmpty(planWithMembers)) {
            planMemberList = setPlanMemberDTOS(planWithMembers);
        }

        return new PlanDetailResponseDTO(planWithMembers, planMemberList);
    }

    public TeamPlanOnDayResponseDTO getFixedPlanOnDay(TeamFixedPlanOnDateRequestDTO planRequest) {
        List<FixedPlanOnDayDTO> fixedPlansOnDay = planRepository.getFixedPlansOnDay(planRequest.getTeamId(), DateAndTimeFormatter.stringWithDotToStringWithoutDot(planRequest.getSearchDate()));

        return new TeamPlanOnDayResponseDTO(fixedPlansOnDay);
    }

    public PlanDateOnMonthResponseDTO getFixedPlanInMonth(TeamFixedPlanOnDateRequestDTO planRequest) {
        List<Date> fixedPlansInMonth = planRepository.getFixedPlansInMonth(planRequest.getTeamId(), DateAndTimeFormatter.stringWithDotToStringWithoutDot(planRequest.getSearchDate()));
        List<Integer> planDates = datesToIntegerList(fixedPlansInMonth);

        return new PlanDateOnMonthResponseDTO(planDates);
    }

    public WaitingPlanResponseDTO getTeamWaitingPlan(TeamWaitingPlanRequestDTO planRequest) {
        List<WaitingPlanDTO> teamWaitingPlans = planRepository.getTeamWaitingPlan(planRequest.getTeamId());

        return new WaitingPlanResponseDTO(teamWaitingPlans);
    }

    public SideMenuFixedPlanResponseDTO getFixedPastPlan(Long memberId, Long teamId) {
        List<SideMenuFixedPlanDTO> fixedPastPlans = planRepository.getFixedPastPlans(teamId, memberId);
        return new SideMenuFixedPlanResponseDTO(fixedPastPlans);
    }

    public SideMenuFixedPlanResponseDTO getFixedOncomingPlan(Long memberId, Long teamId) {
        List<SideMenuFixedPlanDTO> fixedOncomingPlans = planRepository.getFixedOncomingPlans(teamId, memberId);
        return new SideMenuFixedPlanResponseDTO(fixedOncomingPlans);
    }

    public FixPlanResponseDTO fixPlan(Long memberId, FixPlanRequestDTO planRequest) {
        Plan plan = planRepository.findById(planRequest.getPlanId());

        //모임에 속한 사용자인지 검사
        validateMemberIsTeamMember(teamMemberRepository, plan.getTeamId(), memberId);
        //이미 확정된 약속인지 검사
        validatePlanIsAlreadyFixed(plan);
        //범위 내에 존재하는 날짜인지 검사
        validateDateInPeriod(planRequest.getFixedDate(), plan);

        Time fixedTime = timeIntegerToTime(planRequest);

        //Plan에 확정 날짜와 시간, status update - 변경 감지 이용
        plan.fixPlan(planRequest.getFixedDate(), fixedTime);

        //확정한 약속의 구성원
        setPlanMember(planRequest.getMemberIds(), plan);

        //해당 약속의 모든 PlanMemberTime 삭제
        deletePlanMemberTime(plan);

        return new FixPlanResponseDTO(plan);
    }

    private void setPlanMember(List<Long> memberIds, Plan plan) {
        //cascade로 영속화
        memberIds.forEach(memberId
                -> PlanMember.createPlanMember(plan, memberRepository.findById(memberId)));
    }

    private void deletePlanMemberTime(Plan plan) {
        int deletedPlanMemberTimeCount = planMemberTimeRepository.deleteOnPlan(plan);
        log.info("약속을 확정하며 삭제된 PlanMemberTime 개수: {}", deletedPlanMemberTimeCount);
    }

    public OneMemberAvailableTimeResponseDTO getOneMemberAvailableTimeSlot(Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId);

        // 대기 중인 약속일 때만 나의 가능한 시간 조회
        validatePlanIsWaiting(plan);

        // 유저의 시간 정보 유무 조회
        if (!planMemberTimeRepository.isMemberAvailableTimeDataExist(plan, memberId)) {
            return OneMemberAvailableTimeResponseDTO.notRegistered(planId, memberId);
        }

        // 가능한 시간 정보 조회
        List<PlanMemberTime> planMemberTimes = planMemberTimeRepository.findByTeamAndMemberId(plan, memberId);
        List<OneMemberAvailableTimeDTO> timeSlot = getAllMemberAvailableTimeSlot(planMemberTimes);

        // 가능한 시간이 존재하는지
        Boolean hasAvailableTime = timeSlot.stream()
                .anyMatch(timeSlotOnDay -> !timeSlotOnDay.getAvailableTimes().isEmpty());

        return OneMemberAvailableTimeResponseDTO.registered(planId, memberId, hasAvailableTime, timeSlot);
    }

    public AllMemberAvailableTimeResponseDTO getAllMemberAvailableTimeSlot(Long planId) {
        Plan plan = planRepository.findById(planId);

        // 대기 중인 약속일 때만 구성원의 가능한 시간 조회
        validatePlanIsWaiting(plan);

        Date periodStartDate = plan.getStartPeriod();

        //모임 구성원 수 -> 구간 나눌 때 필요
        Long teamId = plan.getTeam().getId();
        Long teamMemberTotalCount = teamMemberRepository.getCount(teamId);

        //구간별 최대 인원수 구하기
        Map<String, Long> endNumberForEachSection = getIntervalEndBySection(teamMemberTotalCount);

        //가능한 시간 조회해서 Response에 포함되는 List<MemberDateTimeResponseDTO> 추출
        List<MemberDateTimeDTO> memberDateTimes = new ArrayList<>(ONE_WEEK_DAYS);

        //7일 각각에 대하여, 시간을 입력한 모든 구성원의 가능한 시간 조회
        Date date = periodStartDate;
        for (int i = 0; i < ONE_WEEK_DAYS; i++) {
            //하루에 대한 날짜와 가능한 구성원 & 시간 정보 추가
            List<AvailableMemberDTO> memberResponses = getAvailableTimeOnDay(plan, date, teamMemberTotalCount, endNumberForEachSection);
            memberDateTimes.add(new MemberDateTimeDTO(date, memberResponses));
            //========== 하루의 가능한 구성원 및 시간 정보 추가 완료 ==========//

            //날짜 갱신: 하루 더하기
            date = Date.valueOf(date.toLocalDate().plusDays(1));
        }

        return new AllMemberAvailableTimeResponseDTO(teamId, plan, endNumberForEachSection, memberDateTimes);
    }

    private List<AvailableMemberDTO> getAvailableTimeOnDay(Plan plan, Date date, Long teamMemberTotalCount, Map<String, Long> endNumberForEachSection) {
        //24시간 - ArrayList 초기화
        int[] membersCount = new int[ONE_DAY_HOURS];
        List<AvailableMemberDTO> memberResponses = new ArrayList<>(ONE_DAY_HOURS);
        ArrayList<ArrayList<String>> memberNames = new ArrayList<>(ONE_DAY_HOURS);
        ArrayList<ArrayList<Long>> memberIds = new ArrayList<>(ONE_DAY_HOURS);
        for (int i = 0; i < ONE_DAY_HOURS; i++) {
            memberNames.add(new ArrayList<>());
            memberIds.add(new ArrayList<>());
        }

        //해당 약속의 한 날짜에 대하여, 시간을 입력한 모든 구성원 정보 및 각 구성원의 가능한 시간 조회
        //[ { memberId, memberName, possibleTime }, ... ] / [ { 1, "이름" , "1,2,3,4" }, ... ]
        List<MemberAvailableTimeDTO> memberAvailableTimes = planMemberTimeRepository.findMemberAndAvailableTimeOnDay(plan, date);
        for (MemberAvailableTimeDTO memberAvailableTime : memberAvailableTimes) {
            String possibleTime = memberAvailableTime.getPossibleTime();  // "1,2,3,4"

            if (possibleTime.isBlank()) continue; //가능한 시간이 없는 경우 넘어감

            List<Integer> possibleTimes = Arrays.stream(possibleTime.split(AVAILABLE_TIME_SPLIT_REGEX))
                    .map(Integer::parseInt)
                    .toList();// ["1", "2", "3", "4"]

            possibleTimes.stream()
                    .forEach(time -> {
                        //시간이 범위를 벗어나는지 검사
                        validateTime(time);

                        //[1, 0, 0, ... 0, 0]  각 시간에 가능한 구성원의 수
                        membersCount[time]++;
                        //각 시간에 가능한 구성원 정보
                        memberNames.get(time).add(memberAvailableTime.getMemberName());
                        memberIds.get(time).add(memberAvailableTime.getMemberId());
                    });
        }

        int[] section = membersCount;
        if (teamMemberTotalCount >= SECTION_DIVISOR) {
            section = setSectionForEachTime(endNumberForEachSection, membersCount);
        }

        //0시부터 23시까지 가능한 시간 정보 저장
        for (int time = MIN_TIME_NUMBER; time <= MAX_TIME_NUMBER; time++) {
            AvailableMemberDTO memberResponse = new AvailableMemberDTO(time, section[time], memberNames.get(time), memberIds.get(time));
            memberResponses.add(memberResponse);
        }
        return memberResponses;
    }

    public void registerAvailableTime(Long memberId, RegisterAvailableTimeRequestDTO planRequest) {
        Plan plan = planRepository.findById(planRequest.getPlanId());
        Member member = memberRepository.findById(memberId);

        //대기 중인 약속일 때만 시간 저장
        validatePlanIsWaiting(plan);

        //7개의 날에 대하여 가능한 시간 저장
        for (AvailableDateTimeDTO availableDateTime : planRequest.getAvailableDateTimes()) {
            savePlanMemberTime(availableDateTime, plan, member);
        }
    }

    private void savePlanMemberTime(AvailableDateTimeDTO availableDateTime, Plan plan, Member member) {
        String availableDate = availableDateTime.getDate();
        Date formattedAvailableDate = DateAndTimeFormatter.stringWithoutDotToDate(DateAndTimeFormatter.stringWithDotToStringWithoutDot(availableDate));

        //해당 날짜의 기존 데이터 삭제
        planMemberTimeRepository.deleteExistingAvailableTimeOnDate(plan, member, formattedAvailableDate);

        //7개의 날에 대하여 가능한 시간을 문자열로 변환
        String availableTimes = setAvailableTimeToString(availableDateTime.getTime());

        //가능한 시간 저장
        PlanMemberTime planMemberTime = PlanMemberTime.createPlanMemberTime(plan, member, formattedAvailableDate, availableTimes);
    }

    // 특정 날짜에 가능한 시간을 문자열로 변환
    private String setAvailableTimeToString(List<Integer> times) {
        //가능한 시간이 없는 경우
        if (times.isEmpty()) return NO_AVAILABLE_TIME;

        return times.stream()
                .map(time -> Integer.toString(time))
                .collect(Collectors.joining(AVAILABLE_TIME_SPLIT_REGEX));
    }

    public void updateFixedPlan(Long memberId, UpdateFixedPlanRequestDTO planRequest) {
        Plan plan = planRepository.findById(planRequest.getPlanId());

        //확정된 약속일 때만 수정
        validatePlanIsFixed(plan);

        //약속 수정 권한이 있는지 확인(모임 구성원 여부)
        validateMemberIsTeamMember(teamMemberRepository, plan.getTeamId(), memberId);

        //약속 이름, 약속 날짜/시간 update
        plan.updateFixedPlan(planRequest.getPlanName(), planRequest.getDate(), timeWithSeconds(planRequest.getTime()));

        //구성원 update
        //1. 기존 구성원 삭제
        planMemberRepository.deleteOnPlan(plan);
        //2. 새로운 구성원 추가
        setPlanMember(planRequest.getMemberIds(), plan);
    }

    public void updateWaitingPlan(Long memberId, UpdateWaitingPlanRequestDTO planRequest) {
        Plan plan = planRepository.findById(planRequest.getPlanId());

        //대기 중인 약속일 때만 나의 가능한 시간 조회
        validatePlanIsWaiting(plan);

        //약속 수정 권한이 있는지 확인(모임 구성원 여부)
        validateMemberIsTeamMember(teamMemberRepository, plan.getTeamId(), memberId);

        plan.updateWaitingPlan(planRequest.getPlanName());
    }

    public void deletePlan(Long memberId, Long planId) {
        Plan plan = planRepository.findById(planId);

        //약속 수정 권한이 있는지 확인(모임 구성원 여부)
        validateMemberIsTeamMember(teamMemberRepository, plan.getTeamId(), memberId);

        //약속 삭제
        //변경 감지로 가능하지만 delete 쿼리가 일일이 실행되기 때문에 각각 벌크 연산 실행
        //1. 확정 약속 -> plan_member 삭제
        if (plan.isFixedPlan()) {
            planMemberRepository.deleteOnPlan(plan);
        }
        //2. 대기 중인 약속 -> plan_member_time 삭제
        if (!plan.isFixedPlan()) {
            planMemberTimeRepository.deleteOnPlan(plan);
        }

        planRepository.delete(plan);
    }

    public List<MemberIsInResponseDTO> getAllMemberIsInPlan(Long planId) {
        Plan plan = planRepository.findWithMembersById(planId);
        validatePlanIsFixed(plan);

        List<Member> planMembers = plan.getPlanMemberDetails();
        List<Member> teamMembers = teamMemberRepository.findTeamMembersByTeamId(plan.getTeam());

        return teamMembers.stream()
                .map(teamMember -> {
                    boolean isInPlan = planMembers.contains(teamMember);
                    return new MemberIsInResponseDTO(teamMember, isInPlan);
                })
                .toList();
    }
}