package com.example.wakeupmate.place.repository;

import com.example.wakeupmate.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByUserId(Long userId);
}
