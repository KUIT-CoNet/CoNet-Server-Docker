package com.kuit.conet.domain.plan;

import com.kuit.conet.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id") // 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계의 주인
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 다대다(다대일, 일대다) 단방향 연관 관계 / 연관 관계의 주인
    private Member member;

    public static PlanMember createPlanMember(Plan plan, Member member) {
        PlanMember planMember = new PlanMember();
        planMember.plan = plan;
        planMember.member = member;

        plan.addPlanMember(planMember);

        return planMember;
    }

}
