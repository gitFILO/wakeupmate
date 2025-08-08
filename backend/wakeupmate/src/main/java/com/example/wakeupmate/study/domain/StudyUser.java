package com.example.wakeupmate.study.domain;

import com.example.wakeupmate.place.domain.Place;
import com.example.wakeupmate.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_study")
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

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(nullable = false)
    private boolean approved = false;

    public StudyUser(final Study study, final User user) {
        this.study = study;
        this.user = user;
        this.approved = false;
    }

    public StudyUser(final Study study, final User user, final boolean approved) {
        this.study = study;
        this.user = user;
        this.approved = approved;
    }

    public StudyUser(final Study study, final User user, final Place place, final boolean approved) {
        this.study = study;
        this.user = user;
        this.place = place;
        this.approved = approved;
    }

    public void approve() {
        this.approved = true;
    }
}