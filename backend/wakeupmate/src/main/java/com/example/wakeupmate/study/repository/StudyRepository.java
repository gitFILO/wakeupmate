package com.example.wakeupmate.study.repository;

import com.example.wakeupmate.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {
    public Study findByStudyName(String studyName);

    public List<Study> findByAdminId(Long userId);
}
