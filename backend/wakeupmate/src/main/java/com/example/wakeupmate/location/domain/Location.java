package com.example.wakeupmate.location.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Long userId;
    private Long studyId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean isViolating;
    private double distanceFromStudy;
    private LocalDateTime lastUpdated;
} 