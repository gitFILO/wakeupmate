package com.example.wakeupmate.study.service;

import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.study.dto.StudyRequestDto;
import com.example.wakeupmate.study.dto.StudyResponseDto;
import com.example.wakeupmate.study.repository.StudyRepository;
import com.example.wakeupmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;

    public Long createStudy(StudyRequestDto dto, User user) {
        Study study = new Study(dto, user);
        studyRepository.save(study);
        return study.getId();
    }

    public void deleteStudy(Long studyId, User user) {
        Study study = getStudyById(studyId);
        if (!study.getAdmin().getId().equals(user.getId())) throw new RuntimeException("방장만 삭제할 수 있습니다");
        studyRepository.delete(study);
    }

    public void updateStudy(Long studyId, StudyRequestDto dto, User user) {
        Study study = getStudyById(studyId);
        if (!study.getAdmin().getId().equals(user.getId())) throw new RuntimeException("방장만 수정할 수 있습니다");
        study.update(dto);
    }

    public void requestJoin(Long studyId, User user) {
        //TODO: 스터디 참여 요청 로직
    }

    public void approveJoin(Long studyId, Long userId, User admin) {
        //TODO: 스터디 참여 허용 로직
    }

    public StudyResponseDto getStudyDetail(Long studyId, User user) {
        Study study = getStudyById(studyId);
        boolean isParticipant = false;

        return StudyResponseDto.of(study, isParticipant);
    }

    private Study getStudyById(Long id) {
        return studyRepository.findById(id).orElseThrow(() -> new RuntimeException("스터디가 존재하지 않습니다"));
    }
}
