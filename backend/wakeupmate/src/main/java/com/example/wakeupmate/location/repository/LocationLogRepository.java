package com.example.wakeupmate.location.repository;

import com.example.wakeupmate.location.domain.LocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LocationLogRepository extends JpaRepository<LocationLog, Long> {
    
    @Query("SELECT l FROM LocationLog l WHERE l.study.id = :studyId AND l.timestamp >= :since ORDER BY l.timestamp DESC")
    List<LocationLog> findLatestLocationsByStudyId(@Param("studyId") Long studyId, @Param("since") LocalDateTime since);
    
    @Query("SELECT l FROM LocationLog l WHERE l.study.id = :studyId AND l.user.id = :userId ORDER BY l.timestamp DESC")
    List<LocationLog> findByStudyIdAndUserIdOrderByTimestampDesc(@Param("studyId") Long studyId, @Param("userId") Long userId);
    
    @Query("SELECT l FROM LocationLog l WHERE l.study.id = :studyId AND l.user.id = :userId AND l.timestamp >= :since ORDER BY l.timestamp DESC")
    Optional<LocationLog> findLatestLocationByStudyIdAndUserId(@Param("studyId") Long studyId, @Param("userId") Long userId, @Param("since") LocalDateTime since);
} 