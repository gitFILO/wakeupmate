package com.example.wakeupmate.location.controller;

import com.example.wakeupmate.location.dto.LocationResponse;
import com.example.wakeupmate.location.dto.LocationUpdateRequest;
import com.example.wakeupmate.location.service.LocationService;
import com.example.wakeupmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study/{studyId}/location")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationService locationService;
    

    // 사용자 위치 업데이트
    @PostMapping("/update")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long studyId,
            @RequestBody LocationUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @AuthenticationPrincipal User user) {
        
        // 테스트용 헤더 우선 처리
        Long userId = userIdHeader != null ? Long.parseLong(userIdHeader) : user.getId();
        
        locationService.updateUserLocation(
                studyId, 
                userId, 
                request.getLatitude(), 
                request.getLongitude()
        );
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 스터디 내 모든 사용자 위치 조회
     * 15초마다 호출
     */
    @GetMapping("/all")
    public ResponseEntity<List<LocationResponse>> getAllLocations(
            @PathVariable Long studyId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @AuthenticationPrincipal User user) {
        
        // 테스트용 헤더 우선 처리
        Long userId = userIdHeader != null ? Long.parseLong(userIdHeader) : user.getId();

        List<LocationResponse> locations = locationService.getStudyLocations(studyId);
        return ResponseEntity.ok(locations);
    }
} 