package com.example.wakeupmate.location.service;

import com.example.wakeupmate.location.domain.Location;
import com.example.wakeupmate.location.dto.LocationResponse;
import com.example.wakeupmate.place.domain.Place;
import com.example.wakeupmate.study.domain.StudyUser;
import com.example.wakeupmate.study.repository.StudyRepository;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {
    
    private final StudyRepository studyRepository;
    
    @Qualifier("locationCache")
    private final Cache<String, Location> locationCache;

    public void updateUserLocation(Long studyId, Long userId, BigDecimal latitude, BigDecimal longitude) {
        String key = studyId + ":" + userId;

        double distance = calculateDistanceFromStudyPlace(studyId, userId, latitude, longitude);
        boolean isViolating = distance > 50.0; // 50m 기준
        
        Location location = Location.builder()
                .userId(userId)
                .studyId(studyId)
                .latitude(latitude)
                .longitude(longitude)
                .isViolating(isViolating)
                .distanceFromStudy(distance)
                .lastUpdated(LocalDateTime.now())
                .build();
        
        locationCache.put(key, location);
        
        log.info("Location updated - User: {}, Study: {}, Violating: {}, Distance: {}m from study place", 
                userId, studyId, isViolating, Math.round(distance));
    }
    
    public List<LocationResponse> getStudyLocations(Long studyId) {
        return locationCache.asMap().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(studyId + ":"))
                .map(entry -> {
                    Location data = entry.getValue();
                    
                    // 카페인 캐시가 TTL로 자동 만료 처리하므로 추가 확인 불필요
                    // 하지만 혹시 모를 경우를 대비해 30초 체크 유지
                    if (data.getLastUpdated().isBefore(LocalDateTime.now().minusSeconds(30))) {
                        return null;
                    }

                    Place studyPlace = getStudyPlace(studyId, data.getUserId());
                    
                    return LocationResponse.builder()
                            .userId(data.getUserId())
                            .latitude(data.getLatitude())
                            .longitude(data.getLongitude())
                            .isViolating(data.isViolating())
                            .distanceFromStudy(data.getDistanceFromStudy())
                            .lastUpdated(data.getLastUpdated())
                            .placeName(studyPlace != null ? studyPlace.getName() : "장소 미설정")
                            .placeLatitude(studyPlace != null ? studyPlace.getLatitude() : null)
                            .placeLongitude(studyPlace != null ? studyPlace.getLongitude() : null)
                            .build();
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    private double calculateDistanceFromStudyPlace(Long studyId, Long userId, BigDecimal currentLatitude, BigDecimal currentLongitude) {
        Place studyPlace = getStudyPlace(studyId, userId);
        
        if (studyPlace == null) {
            log.warn("User {} has no place set for study {}", userId, studyId);

            return Double.MAX_VALUE;
        }
        
        double distance = calculateDistance(
            currentLatitude, currentLongitude,
            studyPlace.getLatitude(), studyPlace.getLongitude()
        );
        
        log.debug("User {} distance from study place '{}': {}m", 
                userId, studyPlace.getName(), Math.round(distance));
        
        return distance;
    }

    private Place getStudyPlace(Long studyId, Long userId) {
        Optional<StudyUser> studyUser = studyRepository.findStudyUserByStudyIdAndUserId(studyId, userId);
        
        if (studyUser.isPresent()) {
            return studyUser.get().getPlace();
        }
        
        return null;
    }

    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        double earthRadius = 6371000;
        
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }
} 