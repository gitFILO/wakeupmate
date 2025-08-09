package com.example.wakeupmate.location.cache.impl;

import com.example.wakeupmate.location.cache.StudyBuckets;
import com.example.wakeupmate.location.domain.Location;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class BucketsCaffeineOuterChmInner implements StudyBuckets {

    // studyId -> (userId -> Location)
    private final Cache<Long, Map<Long, Location>> outer;

    @Override
    public void put(Long studyId, Long userId, Location location) {
        Map<Long, Location> bucket = outer.get(studyId, k -> new ConcurrentHashMap<>());
        bucket.put(userId, location);
    }

    @Override
    public Map<Long, Location> snapshot(Long studyId) {
        Map<Long, Location> bucket = outer.getIfPresent(studyId);
        if (bucket == null || bucket.isEmpty()) return Collections.emptyMap();

        return Collections.unmodifiableMap(bucket); // 약한 일관성을 통해 O(1) 복사
    }
}
