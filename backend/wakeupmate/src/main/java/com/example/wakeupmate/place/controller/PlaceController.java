package com.example.wakeupmate.place.controller;

import com.example.wakeupmate.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/place")
@RestController
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;
}
