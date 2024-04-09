package com.kuit.conet.repository;

import com.kuit.conet.domain.auth.Platform;
import com.kuit.conet.domain.member.Member;
import com.kuit.conet.domain.member.MemberStatus;
import com.kuit.conet.dto.web.response.member.StorageImgResponseDTO;
import com.kuit.conet.dto.web.response.team.GetTeamMemberResponseDTO;
import com.kuit.conet.dto.web.response.team.GetTeamResponseDTO;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Getter
public class MemberRepository {
    private final EntityManager em;

    @Value("${spring.user.default-image}")
    private String defaultImg;

    public Member findById(Long id) {
        System.out.println("id " + id);
        return em.find(Member.class, id);
    }

    public Long save(Member oauthMember) {
        em.persist(oauthMember);
        return oauthMember.getId();
    }

    public List<Long> findByPlatformAndPlatformId(Platform platform, String platformId) {
        return em.createQuery("select m.id from Member m " +
                        "where m.platform=:platform and m.platformId=:platformId and m.status=:status", Long.class)
                .setParameter("platform", platform)
                .setParameter("platformId", platformId)
                .setParameter("status", MemberStatus.ACTIVE)
                .getResultList();
    }

    public StorageImgResponseDTO getImgUrlResponse(Long memberId) {
        return em.createQuery("select new com.kuit.conet.dto.web.response.member.StorageImgResponseDTO(m.name, m.imgUrl) " +
                        "from Member m where m.id=:memberId and m.status=:status", StorageImgResponseDTO.class)
                .setParameter("memberId", memberId)
                .setParameter("status", MemberStatus.ACTIVE)
                .getSingleResult();
    }

    public Boolean isDefaultImage(Long memberId) {
        return em.createQuery("select case when " +
                        "(select m.imgUrl from Member m where m.id=:memberId and m.status=:status) " +
                        "= :defaultImg " +
                        "then TRUE else FALSE end from Member m", Boolean.class)
                .setParameter("memberId", memberId)
                .setParameter("status", MemberStatus.ACTIVE)
                .setParameter("defaultImg", defaultImg)
                .getSingleResult();
    }

    public List<GetTeamMemberResponseDTO> getMembersByTeamId(Long teamId) {
        return em.createQuery("select new com.kuit.conet.dto.web.response.team.GetTeamMemberResponseDTO(m.id, m.name, m.imgUrl) " +
                        "from TeamMember tm join tm.member m where tm.team.id=:teamId", GetTeamMemberResponseDTO.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }

    public List<GetTeamResponseDTO> getBookmarks(Long memberId) {
        return em.createQuery("select new com.kuit.conet.dto.web.response.team.GetTeamResponseDTO(t, size(t.teamMembers), tm.bookMark) " +
                        "from TeamMember tm join tm.team t where tm.member.id=:memberId and tm.bookMark=:bookMark", GetTeamResponseDTO.class)
                .setParameter("memberId", memberId)
                .setParameter("bookMark", true)
                .getResultList();
    }

    public void deleteMember(Long memberId) {
        em.createQuery("update Member m set m.platform='', m.platformId='', m.imgUrl='', m.serviceTerm=False, m.status='INACTIVE' " +
                        "where m.id=:memberId and m.status='ACTIVE'")
                .setParameter("memberId", memberId)
                .executeUpdate();
        em.flush();
    }
}