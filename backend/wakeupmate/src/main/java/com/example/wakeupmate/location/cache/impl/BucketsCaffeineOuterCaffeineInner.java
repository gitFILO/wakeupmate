package com.example.wakeupmate.location.cache.impl;

import com.example.wakeupmate.location.cache.StudyBuckets;
import com.example.wakeupmate.location.domain.Location;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
@RequiredArgsConstructor
public class BucketsCaffeineOuterCaffeineInner implements StudyBuckets {

    private final Cache<Long, Cache<Long, Location>> outer;
    private final Caffeine<Long, Location> innerBuilder;

    @Override
    public void put(Long studyId, Long userId, Location location) {
        Cache<Long, Location> inner = outer.get(studyId, k -> innerBuilder.build());
        inner.put(userId, location);
    }

    @Override
    public Map<Long, Location> snapshot(Long studyId) {
        Cache<Long, Location> inner = outer.getIfPresent(studyId);
        if (inner == null) return Collections.emptyMap();
        var map = inner.asMap();
        return map.isEmpty() ? Collections.emptyMap() : Map.copyOf(map);
    }
}
