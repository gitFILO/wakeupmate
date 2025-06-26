package com.example.wakeupmate.study.dto;

import com.example.wakeupmate.study.domain.Study;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyResponseDto {
    private String name;
    private String admin;
    private boolean joined;
    private String time;
    private int verificationLevel;

    public static StudyResponseDto of(Study study, boolean joined) {
        return StudyResponseDto.builder()
                .name(study.getStudyName())
                .admin(study.getAdmin().getUsername())
                .joined(joined)
                .time(study.getStudyTime().toString())
                .verificationLevel(study.getVerificationLevel().ordinal())
                .build();
    }
}