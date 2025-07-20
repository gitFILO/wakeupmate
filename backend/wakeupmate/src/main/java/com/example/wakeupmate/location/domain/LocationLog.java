package com.example.wakeupmate.location.domain;

import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "location_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationLog {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, precision = 16, scale = 13)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 16, scale = 13)
    private BigDecimal longitude;

    @Column(nullable = false)
    private Boolean isValid = false;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public LocationLog(Study study, User user, BigDecimal latitude, BigDecimal longitude, Boolean isValid) {
        this.study = study;
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isValid = isValid;
        this.timestamp = LocalDateTime.now();
    }
} 