package com.example.wakeupmate.study.dto;

import com.example.wakeupmate.study.domain.DayOfWeek;
import lombok.Data;

import java.time.LocalTime;
import java.util.Set;

@Data
public class StudyRequestDto {
    private String title;
    private String description;
    private LocalTime wakeUpTime;
    private Set<DayOfWeek> studyDays;
    private Integer verificationLevel;
    private Integer maxParticipants;
    private Integer penalty;
}