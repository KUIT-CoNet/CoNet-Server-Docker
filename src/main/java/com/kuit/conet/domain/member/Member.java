package com.kuit.conet.domain.member;

import com.kuit.conet.domain.auth.Platform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(length = 500)
    private String platformId;

    @Column(length = 500)
    private String imgUrl;

    @Column(columnDefinition = "boolean default false")
    private Boolean serviceTerm; // 필수 약관

    @ColumnDefault("'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public static Member createMember(String email, String defaultImgUrl, Platform platform, String platformId) {
        Member member = new Member();

        member.email = email;
        member.imgUrl = defaultImgUrl;
        member.platform = platform;
        member.platformId = platformId;

        return member;
    }

    public void updateImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void agreeTermAndPutName(String name) {
        this.name = name;
        this.serviceTerm = true;
    }

}
