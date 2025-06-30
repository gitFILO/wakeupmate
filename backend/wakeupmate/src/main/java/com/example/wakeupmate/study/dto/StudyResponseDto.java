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
    private Long id;                         // 스터디 ID 추가
    private String title;                    // name -> title로 변경
    private String description;              // 설명 추가
    private LocalTime wakeUpTime;            // 기상 시간 추가
    private Set<DayOfWeek> studyDays;        // 스터디 요일 추가
    private Integer verificationLevel;       // 인증 강도
    private Integer maxParticipants;         // 최대 참여자 수 추가
    private Integer currentParticipants;     // 현재 참여자 수 추가
    private Integer penalty;                 // 벌금 추가
    private String createdBy;                // admin -> createdBy로 변경
    private Boolean isOwner;                 // 현재 사용자가 소유자인지
    private Boolean isParticipant;           // joined -> isParticipant로 변경

    public static StudyResponseDto of(Study study, boolean isParticipant, boolean isOwner, int currentParticipants) {
        return StudyResponseDto.builder()
                .id(study.getId())
                .title(study.getStudyName())
                .description(study.getDescription())  // Study 엔티티에 description 필드 필요
                .wakeUpTime(study.getStudyTime())
                .studyDays(study.getStudyDays())      // Study 엔티티에 studyDays 필드 필요
                .verificationLevel(study.getVerificationLevel().ordinal() + 1) // 1, 2, 3으로 변환
                .maxParticipants(study.getMaxParticipants())  // Study 엔티티에 maxParticipants 필드 필요
                .currentParticipants(currentParticipants)
                .penalty(study.getPenalty())          // Study 엔티티에 penalty 필드 필요
                .createdBy(study.getAdmin().getUsername())
                .isOwner(isOwner)
                .isParticipant(isParticipant)
                .build();
    }
}