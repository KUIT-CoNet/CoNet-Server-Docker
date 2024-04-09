package com.kuit.conet.dto.web.response.auth;

import com.kuit.conet.domain.member.Member;
import lombok.Getter;

@Getter
public class TermAndNameResponseDTO {
    private String name;
    private String email;
    private Boolean serviceTerm;

    public TermAndNameResponseDTO(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.serviceTerm = member.getServiceTerm();
    }
}
