package com.example.wakeupmate.location.controller;

import com.example.wakeupmate.location.dto.LocationResponse;
import com.example.wakeupmate.location.dto.LocationUpdateRequest;
import com.example.wakeupmate.location.service.LocationService;
import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study/{studyId}/location")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationService locationService;
    private final UserRepository userRepository;
    

    // 사용자 위치 업데이트
    @PostMapping("/update")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long studyId,
            @RequestBody LocationUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @AuthenticationPrincipal User user) {
        
        // 테스트용 헤더 우선 처리
        Long userId = userIdHeader != null ? Long.parseLong(userIdHeader) : user.getId();
        
        // dev 프로파일에서는 사용자 검증 생략
        if (!activeProfiles.contains("dev") && !userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
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
        
        // dev 프로파일에서는 사용자 검증 생략
        if (!activeProfiles.contains("dev") && !userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<LocationResponse> locations = locationService.getStudyLocations(studyId);
        return ResponseEntity.ok(locations);
    }
} 