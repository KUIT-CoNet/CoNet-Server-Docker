package com.kuit.conet.service.validator;

import com.kuit.conet.common.exception.MemberException;
import com.kuit.conet.domain.member.Member;
import com.kuit.conet.domain.member.MemberStatus;
import lombok.extern.slf4j.Slf4j;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
public class MemberValidator {
    public static void validateMemberExisting(Member member) {
        if (member == null) {
            log.error(NOT_FOUND_MEMBER.getMessage());
            throw new MemberException(NOT_FOUND_MEMBER);
        }
    }

    public static void validateActiveMember(Member member) {
        if (!(member.getStatus() == MemberStatus.ACTIVE)) {
            log.error(INACTIVE_MEMBER.getMessage());
            throw new MemberException(INACTIVE_MEMBER);
        }
    }

}
