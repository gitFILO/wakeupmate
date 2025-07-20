package com.example.wakeupmate.study.service;

import com.example.wakeupmate.common.exception.*;
import com.example.wakeupmate.place.domain.Place;
import com.example.wakeupmate.place.repository.PlaceRepository;
import com.example.wakeupmate.place.service.PlaceService;
import com.example.wakeupmate.location.domain.LocationLog;
import com.example.wakeupmate.location.repository.LocationLogRepository;
import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.study.domain.StudyUser;
import com.example.wakeupmate.location.dto.LocationRequestDto;
import com.example.wakeupmate.location.dto.LocationResponseDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PlaceService placeService;
    private final LocationLogRepository locationLogRepository;

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

    @Transactional
    public void setStudyPlace(Long studyId, Long placeId, User user) {
        Study study = getStudyById(studyId);
        Place place = getPlaceById(placeId);

        if (!place.getUser().getId().equals(user.getId())) {
            throw new PlaceException(ExceptionCode.PLACE_OWNER_ONLY);
        }

        boolean isAdmin = study.getAdmin().getId().equals(user.getId());
        boolean isParticipant = studyRepository.isUserParticipating(studyId, user.getId());
        
        if (!isAdmin && !isParticipant) {
            throw new StudyException(ExceptionCode.STUDY_PARTICIPANT_ONLY);
        }

        StudyUser studyUser;
        if (isAdmin && !isParticipant) {
            studyUser = new StudyUser(study, user, place, true);
            study.getParticipants().add(studyUser);
        } else {
            studyUser = findStudyUserByStudyIdAndUserId(studyId, user.getId());
            studyUser.setPlace(place);
        }
    }

    @Transactional
    public boolean updateLocationLog(LocationRequestDto requestDto, User user) {
        Study study = getStudyById(requestDto.getStudyId());

        boolean isAdmin = study.getAdmin().getId().equals(user.getId());
        boolean isParticipant = studyRepository.isUserParticipating(requestDto.getStudyId(), user.getId());
        
        if (!isAdmin && !isParticipant) {
            throw new LocationException(ExceptionCode.LOCATION_ACCESS_DENIED);
        }

        StudyUser studyUser;
        try {
            studyUser = findStudyUserByStudyIdAndUserId(requestDto.getStudyId(), user.getId());
        } catch (StudyException e) {
            if (isAdmin) {
                throw new PlaceException(ExceptionCode.PLACE_NOT_SET_FOR_ADMIN);
            }
            throw e;
        }
        
        Place userPlace = studyUser.getPlace();
        if (userPlace == null) {
            throw new PlaceException(ExceptionCode.PLACE_NOT_SET_FOR_STUDY);
        }

        boolean isValid = placeService.isLocationValid(
                userPlace, 
                requestDto.getLatitude().doubleValue(), 
                requestDto.getLongitude().doubleValue()
        );

        LocationLog locationLog = new LocationLog(
                study,
                user,
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                isValid
        );
        
        locationLogRepository.save(locationLog);
        
        return isValid;
    }

    @Transactional(readOnly = true)
    public List<LocationResponseDto> getStudyLocations(Long studyId, User user) {
        Study study = getStudyById(studyId);

        boolean isAdmin = study.getAdmin().getId().equals(user.getId());
        boolean isParticipant = studyRepository.isUserParticipating(studyId, user.getId());
        
        if (!isAdmin && !isParticipant) {
            throw new LocationException(ExceptionCode.LOCATION_QUERY_ACCESS_DENIED);
        }

        LocalDateTime since = LocalDateTime.now().minusSeconds(30);
        List<LocationLog> locationLogs = locationLogRepository.findLatestLocationsByStudyId(studyId, since);
        
        return locationLogs.stream()
                .map(log -> {
                    try {
                        StudyUser studyUser = findStudyUserByStudyIdAndUserId(studyId, log.getUser().getId());
                        String placeName = studyUser.getPlace() != null ? studyUser.getPlace().getName() : "미설정";
                        return LocationResponseDto.from(log, placeName);
                    } catch (StudyException e) {
                        return LocationResponseDto.from(log, "미설정");
                    }
                })
                .collect(Collectors.toList());
    }

    private void validateNotOwnStudy(Study study, User user) {
        if (study.getAdmin().getId().equals(user.getId())) {
            throw new StudyException(ExceptionCode.CANNOT_JOIN_OWN_STUDY);
        }
    }

    private void validateNotAlreadyParticipating(Long studyId, User user) {
        if (studyRepository.isUserParticipating(studyId, user.getId())) {
            throw new StudyException(ExceptionCode.ALREADY_PARTICIPATING);
        }
    }

    private void validateNotAlreadyRequested(Long studyId, User user) {
        if (studyRepository.existsStudyUserByStudyIdAndUserId(studyId, user.getId())) {
            throw new StudyException(ExceptionCode.ALREADY_REQUESTED);
        }
    }

    private void validateStudyCapacity(Study study) {
        int currentParticipants = studyRepository.countApprovedParticipants(study.getId()) + 1;
        if (currentParticipants >= study.getMaxParticipants()) {
            throw new StudyException(ExceptionCode.STUDY_CAPACITY_FULL);
        }
    }

    private void validateStudyOwner(Study study, User user) {
        if (!study.getAdmin().getId().equals(user.getId())) {
            throw new StudyException(ExceptionCode.STUDY_OWNER_ONLY);
        }
    }

    private void validateJoinRequestExists(Long studyId, Long userId) {
        if (!studyRepository.existsStudyUserByStudyIdAndUserId(studyId, userId)) {
            throw new StudyException(ExceptionCode.JOIN_REQUEST_NOT_FOUND);
        }
    }

    private void validateNotAlreadyApproved(Long studyId, Long userId) {
        if (studyRepository.existsStudyUserByStudyIdAndUserIdAndApproved(studyId, userId, true)) {
            throw new StudyException(ExceptionCode.ALREADY_APPROVED);
        }
    }

    private void validateStudyCapacityForApproval(Study study) {
        int currentParticipants = studyRepository.countApprovedParticipants(study.getId()) + 1;
        if (currentParticipants >= study.getMaxParticipants()) {
            throw new StudyException(ExceptionCode.STUDY_CAPACITY_FULL);
        }
    }

    // helpers
    private Study getStudyById(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new StudyException(ExceptionCode.STUDY_NOT_FOUND));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(ExceptionCode.USER_NOT_FOUND));
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new PlaceException(ExceptionCode.PLACE_NOT_FOUND));
    }

    private StudyUser findStudyUserByStudyIdAndUserId(Long studyId, Long userId) {
        return studyRepository.findStudyUserByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new StudyException(ExceptionCode.JOIN_REQUEST_NOT_FOUND));
    }
}