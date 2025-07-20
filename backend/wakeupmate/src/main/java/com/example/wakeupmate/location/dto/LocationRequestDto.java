package com.example.wakeupmate.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {
    private Long studyId;
    private BigDecimal latitude;
    private BigDecimal longitude;
} 