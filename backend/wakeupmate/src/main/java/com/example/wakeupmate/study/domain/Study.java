package com.example.wakeupmate.study.domain;
import com.example.wakeupmate.global.domain.BaseEntity;
import com.example.wakeupmate.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
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
    private VerificationLevel verificationLevel;

    @ElementCollection(targetClass = DayOfWeek.class)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> frequency = new HashSet<>();

    @Getter
    @Column(nullable = false)
    private java.sql.Time studyTime;

    @Column
    private int fineAmount;

}

