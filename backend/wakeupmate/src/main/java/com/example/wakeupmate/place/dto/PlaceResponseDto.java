package com.example.wakeupmate.place.dto;

import com.example.wakeupmate.place.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponseDto {
    private Long id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isHome;
    private String address;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static PlaceResponseDto from(Place place) {
        return new PlaceResponseDto(
                place.getId(),
                place.getName(),
                place.getLatitude(),
                place.getLongitude(),
                place.getIsHome(),
                place.getAddress(),
                place.getDescription(),
                place.getCreatedAt(),
                place.getModifiedAt()
        );
    }
} 