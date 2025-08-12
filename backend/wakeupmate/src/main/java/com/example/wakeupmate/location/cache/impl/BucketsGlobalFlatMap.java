package com.example.wakeupmate.location.cache.impl;

import com.example.wakeupmate.location.cache.StudyBuckets;
import com.example.wakeupmate.location.domain.Location;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class BucketsGlobalFlatMap implements StudyBuckets {

    // "studyId:userId
    private final Map<String, Location> globalStore;

    @Override
    public void put(Long studyId, Long userId, Location location) {
        globalStore.put(buildKey(studyId, userId), location);
    }

    @Override
    public Map<Long, Location> snapshot(Long studyId) {
        // 전역 스캔
        String prefix = studyId + ":";
        Map<Long, Location> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, Location> e : globalStore.entrySet()) {
            String k = e.getKey();
            if (k.startsWith(prefix)) {
                Location v = e.getValue();
                if (v != null) {
                    result.put(v.getUserId(), v);
                }
            }
        }
        return result.isEmpty() ? Collections.emptyMap() : Map.copyOf(result);
    }

    private String buildKey(Long studyId, Long userId) {
        return studyId + ":" + userId;
    }
}
