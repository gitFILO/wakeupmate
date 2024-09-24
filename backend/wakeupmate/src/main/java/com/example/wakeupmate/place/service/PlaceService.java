package com.example.wakeupmate.place.service;

import com.example.wakeupmate.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PlaceService {
    public final PlaceRepository placeRepository;
}
