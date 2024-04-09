package com.kuit.conet.utils;

import com.kuit.conet.domain.plan.PlanPeriod;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPlanPeriodConverter implements Converter<String, PlanPeriod> {
    @Override
    public PlanPeriod convert(String period) {
        return PlanPeriod.valueOf(period.toUpperCase());
    }
}
