package com.example.wakeupmate.location.dto;

import com.example.wakeupmate.location.domain.LocationLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isValid;
    private LocalDateTime timestamp;
    private String placeName;

    public static LocationResponseDto from(LocationLog locationLog, String placeName) {
        return new LocationResponseDto(
                locationLog.getUser().getId(),
                locationLog.getUser().getUsername(),
                locationLog.getUser().getProfileImageUrl(),
                locationLog.getLatitude(),
                locationLog.getLongitude(),
                locationLog.getIsValid(),
                locationLog.getTimestamp(),
                placeName
        );
    }
} 