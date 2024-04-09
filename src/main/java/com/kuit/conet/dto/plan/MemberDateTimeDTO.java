package com.kuit.conet.dto.plan;

import com.kuit.conet.utils.DateAndTimeFormatter;
import lombok.Getter;

import java.sql.Date;
import java.util.List;

@Getter
public class MemberDateTimeDTO {
    private String date;
    private List<AvailableMemberDTO> sectionAndAvailableTimes;

    public MemberDateTimeDTO(Date date, List<AvailableMemberDTO> sectionAndAvailableTimes) {
        this.date = DateAndTimeFormatter.dateToStringWithDot(date);
        this.sectionAndAvailableTimes = sectionAndAvailableTimes;
    }
}
