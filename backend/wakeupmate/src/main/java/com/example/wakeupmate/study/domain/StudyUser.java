package com.example.wakeupmate.study.domain;

import com.example.wakeupmate.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "UserStudy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyUser {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public StudyUser(final Study study, final User user) {
        this.study = study;
        this.user = user;
    }
}

