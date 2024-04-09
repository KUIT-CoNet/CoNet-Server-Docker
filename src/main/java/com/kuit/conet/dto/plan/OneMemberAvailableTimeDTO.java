package com.kuit.conet.dto.plan;

import com.kuit.conet.utils.DateAndTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class OneMemberAvailableTimeDTO {
    private String date;
    private List<Integer> availableTimes;

    public OneMemberAvailableTimeDTO(Date date) {
        this.date = DateAndTimeFormatter.dateToStringWithDot(date);
    }

    public void setAvailableTimes(List<Integer> availableTimes) {
        this.availableTimes = availableTimes;
    }
}
