package com.example.wakeupmate.study.service;

import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.study.domain.StudyUser;
import com.example.wakeupmate.study.dto.StudyRequestDto;
import com.example.wakeupmate.study.dto.StudyResponseDto;
import com.example.wakeupmate.study.repository.StudyRepository;
import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    public Long createStudy(StudyRequestDto dto, User user) {
        Study study = new Study(dto, user);
        studyRepository.save(study);
        return study.getId();
    }

    public void deleteStudy(Long studyId, User user) {
        Study study = getStudyById(studyId);
        validateStudyOwner(study, user);
        studyRepository.delete(study);
    }

    public void updateStudy(Long studyId, StudyRequestDto dto, User user) {
        Study study = getStudyById(studyId);
        validateStudyOwner(study, user);
        study.update(dto);
    }

    public void requestJoin(Long studyId, User user) {
        Study study = getStudyById(studyId);

        validateNotOwnStudy(study, user);
        validateNotAlreadyParticipating(studyId, user);
        validateNotAlreadyRequested(studyId, user);
        validateStudyCapacity(study);

        StudyUser studyUser = new StudyUser(study, user, false);
        study.getParticipants().add(studyUser);
    }

    public void approveJoin(Long studyId, Long userId, User admin) {
        Study study = getStudyById(studyId);

        validateStudyOwner(study, admin);
        validateJoinRequestExists(studyId, userId);
        validateNotAlreadyApproved(studyId, userId);
        validateStudyCapacityForApproval(study);

        StudyUser studyUser = findStudyUserByStudyIdAndUserId(studyId, userId);
        studyUser.approve();
    }

    @Transactional(readOnly = true)
    public StudyResponseDto getStudyDetail(Long studyId, User user) {
        Study study = getStudyById(studyId);
        boolean isParticipant = studyRepository.isUserParticipating(studyId, user.getId());
        boolean isOwner = study.getAdmin().getId().equals(user.getId());
        int currentParticipants = studyRepository.countApprovedParticipants(studyId) + 1;

        return StudyResponseDto.of(study, isParticipant, isOwner, currentParticipants);
    }

    @Transactional(readOnly = true)
    public Page<StudyResponseDto> getAllStudies(Pageable pageable, User user) {
        Page<Study> studies = studyRepository.findStudiesNotParticipatedByUser(user.getId(), pageable);

        return studies.map(study -> {
            int currentParticipants = studyRepository.countApprovedParticipants(study.getId()) + 1;
            return StudyResponseDto.of(study, false, false, currentParticipants);
        });
    }

    @Transactional(readOnly = true)
    public Page<StudyResponseDto> getMyStudies(Pageable pageable, User user) {
        Page<Study> studies = studyRepository.findStudiesByUserId(user.getId(), pageable);

        return studies.map(study -> {
            boolean isParticipant = studyRepository.isUserParticipating(study.getId(), user.getId());
            boolean isOwner = study.getAdmin().getId().equals(user.getId());
            int currentParticipants = studyRepository.countApprovedParticipants(study.getId()) + 1;

            return StudyResponseDto.of(study, isParticipant, isOwner, currentParticipants);
        });
    }

    // Validates
    private void validateNotOwnStudy(Study study, User user) {
        if (study.getAdmin().getId().equals(user.getId())) {
            throw new RuntimeException("자신이 만든 스터디에는 참여할 수 없습니다");
        }
    }

    private void validateNotAlreadyParticipating(Long studyId, User user) {
        if (studyRepository.isUserParticipating(studyId, user.getId())) {
            throw new RuntimeException("이미 참여 중인 스터디입니다");
        }
    }

    private void validateNotAlreadyRequested(Long studyId, User user) {
        if (studyRepository.existsStudyUserByStudyIdAndUserId(studyId, user.getId())) {
            throw new RuntimeException("이미 참여 요청한 스터디입니다");
        }
    }

    private void validateStudyCapacity(Study study) {
        int currentParticipants = studyRepository.countApprovedParticipants(study.getId()) + 1; // +1은 방장
        if (currentParticipants >= study.getMaxParticipants()) {
            throw new RuntimeException("스터디 정원이 가득찼습니다");
        }
    }

    private void validateStudyOwner(Study study, User user) {
        if (!study.getAdmin().getId().equals(user.getId())) {
            throw new RuntimeException("방장만 이 작업을 수행할 수 있습니다");
        }
    }

    private void validateJoinRequestExists(Long studyId, Long userId) {
        if (!studyRepository.existsStudyUserByStudyIdAndUserId(studyId, userId)) {
            throw new RuntimeException("참여 요청을 찾을 수 없습니다");
        }
    }

    private void validateNotAlreadyApproved(Long studyId, Long userId) {
        if (studyRepository.existsStudyUserByStudyIdAndUserIdAndApproved(studyId, userId, true)) {
            throw new RuntimeException("이미 승인된 사용자입니다");
        }
    }

    private void validateStudyCapacityForApproval(Study study) {
        int currentParticipants = studyRepository.countApprovedParticipants(study.getId()) + 1; // +1은 방장
        if (currentParticipants >= study.getMaxParticipants()) {
            throw new RuntimeException("스터디 정원이 가득찼습니다");
        }
    }

    // helpers
    private Study getStudyById(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("스터디가 존재하지 않습니다"));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    private StudyUser findStudyUserByStudyIdAndUserId(Long studyId, Long userId) {
        return studyRepository.findStudyUserByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new RuntimeException("참여 요청을 찾을 수 없습니다"));
    }
}