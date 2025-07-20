package com.example.wakeupmate.study.dto;

import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.study.domain.DayOfWeek;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
public class StudyResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalTime wakeUpTime;
    private Set<DayOfWeek> studyDays;
    private Integer verificationLevel;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer penalty;
    private String createdBy;
    private Boolean isOwner;
    private Boolean isParticipant;

    public static StudyResponseDto of(Study study, boolean isParticipant, boolean isOwner, int currentParticipants) {
        return StudyResponseDto.builder()
                .id(study.getId())
                .title(study.getStudyName())
                .description(study.getDescription())
                .wakeUpTime(study.getStudyTime())
                .studyDays(study.getStudyDays())
                .verificationLevel(study.getVerificationLevel().ordinal() + 1)
                .maxParticipants(study.getMaxParticipants())
                .currentParticipants(currentParticipants)
                .penalty(study.getPenalty())
                .createdBy(study.getAdmin().getUsername())
                .isOwner(isOwner)
                .isParticipant(isParticipant)
                .build();
    }
}