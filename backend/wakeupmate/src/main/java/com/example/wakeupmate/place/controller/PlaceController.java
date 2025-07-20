package com.example.wakeupmate.place.controller;

import com.example.wakeupmate.place.dto.PlaceRequestDto;
import com.example.wakeupmate.place.dto.PlaceResponseDto;
import com.example.wakeupmate.place.service.PlaceService;
import com.example.wakeupmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/place")
@RestController
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<Long> createPlace(
            @RequestBody PlaceRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        Long placeId = placeService.createPlace(requestDto, user);
        return ResponseEntity.ok(placeId);
    }

    @GetMapping
    public ResponseEntity<List<PlaceResponseDto>> getMyPlaces(
            @AuthenticationPrincipal User user) {
        List<PlaceResponseDto> places = placeService.getMyPlaces(user);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponseDto> getPlace(
            @PathVariable Long placeId,
            @AuthenticationPrincipal User user) {
        PlaceResponseDto place = placeService.getPlaceById(placeId, user);
        return ResponseEntity.ok(place);
    }

    @PutMapping("/{placeId}")
    public ResponseEntity<Void> updatePlace(
            @PathVariable Long placeId,
            @RequestBody PlaceRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        placeService.updatePlace(placeId, requestDto, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> deletePlace(
            @PathVariable Long placeId,
            @AuthenticationPrincipal User user) {
        placeService.deletePlace(placeId, user);
        return ResponseEntity.ok().build();
    }
}
