package com.example.wakeupmate.study.dto;

import com.example.wakeupmate.study.domain.DayOfWeek;
import lombok.Data;

import java.time.LocalTime;
import java.util.Set;

@Data
public class StudyRequestDto {
    private String name;
    private Set<DayOfWeek> days;
    private LocalTime time;
    private int verificationLevel;
}
