package com.example.wakeupmate.study.service;

import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.study.domain.StudyUser;
import com.example.wakeupmate.study.domain.DayOfWeek;
import com.example.wakeupmate.study.domain.VerificationLevel;
import com.example.wakeupmate.study.dto.StudyRequestDto;
import com.example.wakeupmate.study.dto.StudyResponseDto;
import com.example.wakeupmate.study.repository.StudyRepository;
import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudyService studyService;

    @Test
    @DisplayName("스터디를 생성할 수 있다.")
    void createStudy() {
        // given
        StudyRequestDto dto = createStudyRequestDto();
        User user = createTestUser();

        when(studyRepository.save(any(Study.class))).thenAnswer(invocation -> {
            Study savedStudy = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedStudy, "id", 1L);
            return savedStudy;
        });

        // when
        Long result = studyService.createStudy(dto, user);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);
        verify(studyRepository).save(any(Study.class));
    }

    @Test
    @DisplayName("스터디 소유자는 스터디를 삭제할 수 있다.")
    void deleteStudy_Success() {
        // given
        Long studyId = 1L;
        User owner = createTestUser();
        Study study = createTestStudy(owner);

        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));

        // when
        studyService.deleteStudy(studyId, owner);

        // then
        verify(studyRepository).delete(study);
    }

    @Test
    @DisplayName("스터디 소유자가 아닌 사용자는 스터디를 삭제할 수 없다.")
    void deleteStudy_NotOwner() {
        // given
        Long studyId = 1L;
        User owner = createTestUser();
        User otherUser = createTestUser();
        ReflectionTestUtils.setField(otherUser, "id", 2L);
        Study study = createTestStudy(owner);

        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> studyService.deleteStudy(studyId, otherUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("방장만 이 작업을 수행할 수 있습니다");
    }

    @Test
    @DisplayName("스터디를 수정할 수 있다.")
    void updateStudy() {
        // given
        Long studyId = 1L;
        User owner = createTestUser();
        Study study = createTestStudy(owner);
        StudyRequestDto updateDto = createStudyRequestDto();
        updateDto.setTitle("수정된 스터디 제목");

        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));

        // when
        studyService.updateStudy(studyId, updateDto, owner);

        // then
        assertThat(study.getStudyName()).isEqualTo("수정된 스터디 제목");
    }

    @Test
    @DisplayName("사용자는 스터디 참여를 요청할 수 있다.")
    void requestJoin_Success() {
        // given
        Long studyId = 1L;
        User owner = createTestUser();
        User participant = createTestUser();
        ReflectionTestUtils.setField(participant, "id", 2L);
        Study study = createTestStudy(owner);

        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
        when(studyRepository.isUserParticipating(studyId, participant.getId())).thenReturn(false);
        when(studyRepository.existsStudyUserByStudyIdAndUserId(studyId, participant.getId())).thenReturn(false);
        when(studyRepository.countApprovedParticipants(studyId)).thenReturn(5);

        // when
        studyService.requestJoin(studyId, participant);

        // then
        assertThat(study.getParticipants()).hasSize(1);
        assertThat(study.getParticipants().get(0).getUser()).isEqualTo(participant);
        assertThat(study.getParticipants().get(0).isApproved()).isFalse();
    }

    @Test
    @DisplayName("자신이 만든 스터디에는 참여할 수 없다.")
    void requestJoin_OwnStudy() {
        // given
        Long studyId = 1L;
        User owner = createTestUser();
        Study study = createTestStudy(owner);

        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> studyService.requestJoin(studyId, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("자신이 만든 스터디에는 참여할 수 없습니다");
    }

    @Test
    @DisplayName("스터디 방장은 참여 요청을 승인할 수 있다.")
    void approveJoin_Success() {
        // given
        Long studyId = 1L;
        Long userId = 2L;
        User owner = createTestUser();
        User participant = createTestUser();
        ReflectionTestUtils.setField(participant, "id", userId);
        Study study = createTestStudy(owner);
        StudyUser studyUser = new StudyUser(study, participant, false);

        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
        when(studyRepository.existsStudyUserByStudyIdAndUserId(studyId, userId)).thenReturn(true);
        when(studyRepository.existsStudyUserByStudyIdAndUserIdAndApproved(studyId, userId, true)).thenReturn(false);
        when(studyRepository.countApprovedParticipants(studyId)).thenReturn(5);
        when(studyRepository.findStudyUserByStudyIdAndUserId(studyId, userId)).thenReturn(Optional.of(studyUser));

        // when
        studyService.approveJoin(studyId, userId, owner);

        // then
        assertThat(studyUser.isApproved()).isTrue();
    }

    @Test
    @DisplayName("스터디 상세 정보를 조회할 수 있다.")
    void getStudyDetail() {
        // given
        Long studyId = 1L;
        User user = createTestUser();
        Study study = createTestStudy(user);

        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
        when(studyRepository.isUserParticipating(studyId, user.getId())).thenReturn(false);
        when(studyRepository.countApprovedParticipants(studyId)).thenReturn(5);

        // when
        StudyResponseDto result = studyService.getStudyDetail(studyId, user);

        // then
        assertThat(result.getId()).isEqualTo(studyId);
        assertThat(result.getTitle()).isEqualTo(study.getStudyName());
        assertThat(result.getIsOwner()).isTrue();
        assertThat(result.getCurrentParticipants()).isEqualTo(6);
    }

    @Test
    @DisplayName("모든 스터디 목록을 조회할 수 있다.")
    void getAllStudies() {
        // given
        User user = createTestUser();
        Study study1 = createTestStudy(user);
        Study study2 = createTestStudy(user);
        Page<Study> studiesPage = new PageImpl<>(List.of(study1, study2));

        when(studyRepository.findStudiesNotParticipatedByUser(anyLong(), any(Pageable.class)))
                .thenReturn(studiesPage);
        when(studyRepository.countApprovedParticipants(anyLong())).thenReturn(5);

        // when
        Page<StudyResponseDto> result = studyService.getAllStudies(Pageable.unpaged(), user);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCurrentParticipants()).isEqualTo(6);
    }

    private StudyRequestDto createStudyRequestDto() {
        StudyRequestDto dto = new StudyRequestDto();
        dto.setTitle("아침 기상 스터디");
        dto.setDescription("매일 아침 6시에 일어나는 스터디입니다.");
        dto.setWakeUpTime(LocalTime.of(6, 0));
        dto.setStudyDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
        dto.setVerificationLevel(1);
        dto.setMaxParticipants(10);
        dto.setPenalty(2000);
        return dto;
    }

    private User createTestUser() {
        User user = User.builder()
                .socialLoginId("kakao_123456789")
                .username("테스트사용자")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.jpg")
                .role("ROLE_USER")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Study createTestStudy(User admin) {
        StudyRequestDto dto = createStudyRequestDto();
        Study study = new Study(dto, admin);
        ReflectionTestUtils.setField(study, "id", 1L);
        return study;
    }
} 