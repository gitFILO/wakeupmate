package com.example.wakeupmate.common.config;

import com.example.wakeupmate.location.cache.StudyBuckets;
import com.example.wakeupmate.location.cache.impl.BucketsCaffeineOuterChmInner;
import com.example.wakeupmate.location.cache.impl.BucketsCaffeineOuterCaffeineInner;
import com.example.wakeupmate.location.cache.impl.BucketsGlobalFlatMap;
import com.example.wakeupmate.location.domain.Location;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class LocationBucketConfig {

    // Caffeine (studyId -> Map<userId, Location>)
    @Bean
    public Cache<Long, Map<Long, Location>> outerStudyBucketsForChm() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10))
                .maximumSize(200_000)
                .recordStats()
                .build();
    }

    // Caffeine (studyId -> Cache<userId, Location>)
    @Bean
    public Cache<Long, Cache<Long, Location>> outerStudyBucketsForCaffeine() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10))
                .maximumSize(200_000)
                .recordStats()
                .build();
    }

    @Bean
    public Caffeine<Object, Object> innerBucketBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(30))
                .maximumSize(64)
                .recordStats();
    }

    // 전역 유저 관리 캐시
    @Bean(name = "globalLocationStore")
    public Map<String, Location> globalLocationStore() {
        return new ConcurrentHashMap<>();
    }

    // application.yml -> location.cache.impl
    // val: chm 또는 미설정
    @Bean
    @ConditionalOnProperty(prefix = "location.cache", name = "impl", havingValue = "chm", matchIfMissing = true)
    public StudyBuckets studyBucketsChm(Cache<Long, Map<Long, Location>> outerStudyBucketsForChm) {
        return new BucketsCaffeineOuterChmInner(outerStudyBucketsForChm);
    }

    // val: caffeine
    @Bean
    @ConditionalOnProperty(prefix = "location.cache", name = "impl", havingValue = "caffeine")
    public StudyBuckets studyBucketsCaffeine(Cache<Long, Cache<Long, Location>> outerStudyBucketsForCaffeine,
                                             Caffeine<Long, Location>  innerBucketBuilder) {
        return new BucketsCaffeineOuterCaffeineInner(outerStudyBucketsForCaffeine, innerBucketBuilder);
    }

    // val: global
    @Bean
    @ConditionalOnProperty(prefix = "location.cache", name = "impl", havingValue = "global")
    public StudyBuckets studyBucketsGlobal(Map<String, Location> globalLocationStore) {
        return new BucketsGlobalFlatMap(globalLocationStore);
    }
}
