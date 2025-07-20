package com.example.wakeupmate.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequestDto {
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isHome;
    private String address;
    private String description;
} 