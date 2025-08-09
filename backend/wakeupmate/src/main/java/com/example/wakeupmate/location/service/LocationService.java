package com.example.wakeupmate.location.service;

import com.example.wakeupmate.location.cache.StudyBuckets;
import com.example.wakeupmate.location.domain.Location;
import com.example.wakeupmate.location.dto.LocationResponse;
import com.example.wakeupmate.place.domain.Place;
import com.example.wakeupmate.study.domain.StudyUser;
import com.example.wakeupmate.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {
    
    private final StudyRepository studyRepository;
    private final StudyBuckets studyBuckets;


    private static final double VIOLATION_M = 50.0;
    private static final Duration TTL = Duration.ofSeconds(30);

    public void updateUserLocation(Long studyId, Long userId, BigDecimal latitude, BigDecimal longitude) {

        double distance = calculateDistanceFromStudyPlace(studyId, userId, latitude, longitude);
        boolean isViolating = distance > VIOLATION_M;

        Location location = Location.builder()
                .userId(userId)
                .studyId(studyId)
                .latitude(latitude)
                .longitude(longitude)
                .isViolating(isViolating)
                .distanceFromStudy(distance)
                .lastUpdated(LocalDateTime.now())
                .build();

        studyBuckets.put(studyId, userId, location);
        
        log.info("Location updated - User: {}, Study: {}, Violating: {}, Distance: {}m from study place", 
                userId, studyId, isViolating, Math.round(distance));
    }
    
    public List<LocationResponse> getStudyLocations(Long studyId) {

        Map<Long, Location> bucket = studyBuckets.snapshot(studyId);
        if (bucket.isEmpty()) return List.of();

        LocalDateTime cutoff = LocalDateTime.now().minus(TTL);
        List<LocationResponse> out = new ArrayList<>(bucket.size());

        for (Location l : bucket.values()) {
            boolean online = !l.getLastUpdated().isBefore(cutoff);
            Place p = getStudyPlace(studyId, l.getUserId());
            out.add(LocationResponse.builder()
                    .userId(l.getUserId())
                    .latitude(online ? l.getLatitude() : null)
                    .longitude(online ? l.getLongitude() : null)
                    .distanceFromStudy(online ? l.getDistanceFromStudy() : null)
                    .isViolating(online && l.isViolating())
                    .lastUpdated(l.getLastUpdated())
                    .placeName(p != null ? p.getName() : "장소 미설정")
                    .placeLatitude(p != null ? p.getLatitude() : null)
                    .placeLongitude(p != null ? p.getLongitude() : null)
                    //.online(online) // 온라인 여부 검사 추가 예정
                    .build());
        }
        return out;
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