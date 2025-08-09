package com.example.wakeupmate.location.cache;

import com.example.wakeupmate.location.domain.Location;

import java.util.Map;

public interface StudyBuckets {
    void put(Long studyId, Long userId, Location location);

    Map<Long, Location> snapshot(Long studyId);
}
