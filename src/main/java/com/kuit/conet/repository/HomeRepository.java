package com.kuit.conet.repository;

import com.kuit.conet.domain.plan.PlanStatus;
import com.kuit.conet.dto.home.HomeFixedPlanOnDayDTO;
import com.kuit.conet.dto.plan.WaitingPlanDTO;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Getter
public class HomeRepository {
    private final EntityManager em;

    public List<Date> getHomeFixedPlansInMonth(Long memberId, String searchDate) {
        // 해당 년, 월에 유저가 포함된 약속(유저가 꼭 포함되어 있는 약속이어야 함) -> fixed_date 만 distinct 로 검색
        // team_member(memberId, status) -> plan(teamId, fixed_date, status)

        return em.createQuery("select distinct p.fixedDate " +
                        "from PlanMember pm join Plan p on pm.plan.id = p.id " +
                        "where pm.member.id = :memberId " +
                        "and p.status = :status " +
                        "and FUNCTION('DATE_FORMAT', p.fixedDate, '%Y-%m') = :searchDate", Date.class)
                .setParameter("memberId", memberId)
                .setParameter("status", PlanStatus.FIXED)
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public List<HomeFixedPlanOnDayDTO> getHomeFixedPlansOnDay(Long memberId, String searchDate) {
        return em.createQuery("select new com.kuit.conet.dto.home.HomeFixedPlanOnDayDTO(p.id, p.fixedTime, p.team.name, p.name) " +
                        "from PlanMember pm join Plan p on pm.plan.id = p.id " +
                        "where pm.member.id = :memberId " +
                        "and p.status=:status " +
                        "and FUNCTION('DATE_FORMAT', p.fixedDate, '%Y-%m-%d')=:searchDate " +
                        "order by p.fixedTime", HomeFixedPlanOnDayDTO.class)
                .setParameter("memberId", memberId)
                .setParameter("status", PlanStatus.FIXED)
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public List<WaitingPlanDTO> getHomeWaitingPlans(Long memberId) {
        // 유저가 속한 모든 모임의 tm.memberId=:memberId & tm.team_id = p.team_id
        // 모든 대기 중인 약속 중에서 p.status=WAITING
        // 시작 날짜가 오늘 이후 plan_start_period >= current_date();

        return em.createQuery("select new com.kuit.conet.dto.plan.WaitingPlanDTO(p.id, p.startPeriod, p.endPeriod, p.team.name, p.name) " +
                        "from TeamMember tm join Plan p on tm.team.id=p.team.id " +
                        "where tm.member.id=:memberId " +
                        "and p.status=:status " +
                        "and p.startPeriod>=current_date() " +
                        "order by p.startPeriod", WaitingPlanDTO.class)
                .setParameter("memberId", memberId)
                .setParameter("status", PlanStatus.WAITING)
                .getResultList();
    }
}
