package com.example.wakeupmate.study.repository;

import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.study.domain.StudyUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    Study findByStudyName(String studyName);

    List<Study> findByAdminId(Long userId);

    Page<Study> findAll(Pageable pageable);

    @Query("SELECT s FROM Study s WHERE s.admin.id = :userId OR s.id IN " +
            "(SELECT su.study.id FROM StudyUser su WHERE su.user.id = :userId AND su.approved = true)")
    Page<Study> findStudiesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM Study s WHERE s.admin.id != :userId AND s.id NOT IN " +
            "(SELECT su.study.id FROM StudyUser su WHERE su.user.id = :userId)")
    Page<Study> findStudiesNotParticipatedByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(su) FROM StudyUser su WHERE su.study.id = :studyId AND su.approved = true")
    int countApprovedParticipants(@Param("studyId") Long studyId);

    @Query("SELECT COUNT(su) > 0 FROM StudyUser su WHERE su.study.id = :studyId AND su.user.id = :userId AND su.approved = true")
    boolean isUserParticipating(@Param("studyId") Long studyId, @Param("userId") Long userId);

    @Query("SELECT s.admin.id = :userId FROM Study s WHERE s.id = :studyId")
    boolean isUserOwner(@Param("studyId") Long studyId, @Param("userId") Long userId);

    @Query("SELECT COUNT(su) > 0 FROM StudyUser su WHERE su.study.id = :studyId AND su.user.id = :userId")
    boolean existsStudyUserByStudyIdAndUserId(@Param("studyId") Long studyId, @Param("userId") Long userId);

    @Query("SELECT COUNT(su) > 0 FROM StudyUser su WHERE su.study.id = :studyId AND su.user.id = :userId AND su.approved = :approved")
    boolean existsStudyUserByStudyIdAndUserIdAndApproved(@Param("studyId") Long studyId, @Param("userId") Long userId, @Param("approved") boolean approved);

    @Query("SELECT su FROM StudyUser su WHERE su.study.id = :studyId AND su.user.id = :userId")
    Optional<StudyUser> findStudyUserByStudyIdAndUserId(@Param("studyId") Long studyId, @Param("userId") Long userId);

}