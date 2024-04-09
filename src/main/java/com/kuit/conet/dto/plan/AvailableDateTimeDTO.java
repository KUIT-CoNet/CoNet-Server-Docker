package com.kuit.conet.dto.plan;

import lombok.Getter;

import java.util.List;

@Getter
public class AvailableDateTimeDTO {
    private String date;
    private List<Integer> time;
}
