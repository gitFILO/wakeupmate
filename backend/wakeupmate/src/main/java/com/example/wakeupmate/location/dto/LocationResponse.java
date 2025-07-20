package com.example.wakeupmate.location.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LocationResponse {
    private Long userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean isViolating;
    private double distanceFromStudy;
    private LocalDateTime lastUpdated;

    private String placeName;
    private BigDecimal placeLatitude;
    private BigDecimal placeLongitude;
} 