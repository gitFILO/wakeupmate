package com.example.wakeupmate.study.domain;

import com.example.wakeupmate.global.domain.BaseEntity;
import com.example.wakeupmate.study.dto.StudyRequestDto;
import com.example.wakeupmate.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Studies")
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studyName;

    @Column(length = 1000)  // 설명 필드 추가
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyUser> participants = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationLevel verificationLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyState studyState = StudyState.ACTIVE;

    @ElementCollection(targetClass = DayOfWeek.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "study_days", joinColumns = @JoinColumn(name = "study_id"))
    @Column(name = "day")
    private Set<DayOfWeek> studyDays = new HashSet<>();

    @Column(nullable = false)
    private LocalTime studyTime;

    @Column(nullable = false)
    private Integer maxParticipants = 10;

    @Column(nullable = false)
    private Integer penalty = 2000;

    public Study(StudyRequestDto dto, User admin) {
        this.studyName = dto.getTitle();
        this.description = dto.getDescription();
        this.admin = admin;
        this.studyTime = dto.getWakeUpTime();
        this.verificationLevel = VerificationLevel.of(dto.getVerificationLevel());
        this.studyDays = Set.copyOf(dto.getStudyDays());
        this.maxParticipants = dto.getMaxParticipants() != null ? dto.getMaxParticipants() : 10;
        this.penalty = dto.getPenalty() != null ? dto.getPenalty() : 2000;
        this.studyState = StudyState.ACTIVE;
    }

    public void update(StudyRequestDto dto) {
        this.studyName = dto.getTitle();
        this.description = dto.getDescription();
        this.studyTime = dto.getWakeUpTime();
        this.verificationLevel = VerificationLevel.of(dto.getVerificationLevel());
        this.studyDays = Set.copyOf(dto.getStudyDays());
        if (dto.getMaxParticipants() != null) {
            this.maxParticipants = dto.getMaxParticipants();
        }
        if (dto.getPenalty() != null) {
            this.penalty = dto.getPenalty();
        }
    }
}