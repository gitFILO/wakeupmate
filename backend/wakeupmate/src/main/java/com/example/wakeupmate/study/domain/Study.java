package com.example.wakeupmate.study.domain;

import com.example.wakeupmate.global.domain.BaseEntity;
import com.example.wakeupmate.study.dto.StudyRequestDto;
import com.example.wakeupmate.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
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
    private Set<DayOfWeek> frequency = new HashSet<>();

    @Column(nullable = false)
    private Time studyTime;

    @Column(nullable = false)
    private int fineAmount = 2000;

    public Study(StudyRequestDto dto, User admin) {
        this.studyName = dto.getName();
        this.admin = admin;
        this.studyTime = Time.valueOf(dto.getTime());
        this.verificationLevel = VerificationLevel.of(dto.getVerificationLevel());
        this.frequency = Set.copyOf(dto.getDays());
        this.studyState = StudyState.ACTIVE;
    }

    public void update(StudyRequestDto dto) {
        this.studyName = dto.getName();
        this.studyTime = Time.valueOf(dto.getTime());
        this.verificationLevel = VerificationLevel.of(dto.getVerificationLevel());
        this.frequency = Set.copyOf(dto.getDays());
    }
}
