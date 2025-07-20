package com.example.wakeupmate.place.service;

import com.example.wakeupmate.common.exception.ExceptionCode;
import com.example.wakeupmate.common.exception.PlaceException;
import com.example.wakeupmate.place.domain.Place;
import com.example.wakeupmate.place.dto.PlaceRequestDto;
import com.example.wakeupmate.place.dto.PlaceResponseDto;
import com.example.wakeupmate.place.repository.PlaceRepository;
import com.example.wakeupmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Transactional
    public Long createPlace(PlaceRequestDto requestDto, User user) {
        Place place = new Place(
                requestDto.getName(),
                user,
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getIsHome(),
                requestDto.getAddress(),
                requestDto.getDescription()
        );

        Place savedPlace = placeRepository.save(place);
        return savedPlace.getId();
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseDto> getMyPlaces(User user) {
        List<Place> places = placeRepository.findByUserId(user.getId());
        return places.stream()
                .map(PlaceResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlaceResponseDto getPlaceById(Long placeId, User user) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(ExceptionCode.PLACE_NOT_FOUND));
        
        if (!place.getUser().getId().equals(user.getId())) {
            throw new PlaceException(ExceptionCode.PLACE_OWNER_ONLY);
        }
        
        return PlaceResponseDto.from(place);
    }

    @Transactional
    public void updatePlace(Long placeId, PlaceRequestDto requestDto, User user) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(ExceptionCode.PLACE_NOT_FOUND));
        
        if (!place.getUser().getId().equals(user.getId())) {
            throw new PlaceException(ExceptionCode.PLACE_OWNER_ONLY);
        }
        
        place.update(
                requestDto.getName(),
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getIsHome(),
                requestDto.getAddress(),
                requestDto.getDescription()
        );
    }

    @Transactional
    public void deletePlace(Long placeId, User user) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(ExceptionCode.PLACE_NOT_FOUND));
        
        if (!place.getUser().getId().equals(user.getId())) {
            throw new PlaceException(ExceptionCode.PLACE_OWNER_ONLY);
        }
        
        placeRepository.delete(place);
    }

    // 거리 계산 메서드 (하버사인 공식)
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c * 1000;
    }

    // 위치 검증 메서드 (50미터 이내면 유효)
    public boolean isLocationValid(Place place, double currentLat, double currentLon) {
        double distance = calculateDistance(
                place.getLatitude().doubleValue(),
                place.getLongitude().doubleValue(),
                currentLat,
                currentLon
        );
        return distance <= 50; // 50미터 이내
    }
}
