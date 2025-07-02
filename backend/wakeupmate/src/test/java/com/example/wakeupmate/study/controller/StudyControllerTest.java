package com.example.wakeupmate.study.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.wakeupmate.study.dto.StudyRequestDto;
import com.example.wakeupmate.study.dto.StudyResponseDto;
import com.example.wakeupmate.study.service.StudyService;
import com.example.wakeupmate.study.domain.DayOfWeek;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class StudyControllerTest {

    @Mock
    private StudyService studyService;

    @InjectMocks
    private StudyController studyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(studyController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("스터디를 생성할 수 있다.")
    void createStudy() throws Exception {
        // given
        StudyRequestDto requestDto = createStudyRequestDto();
        Long studyId = 1L;

        Mockito.when(studyService.createStudy(any(StudyRequestDto.class), any()))
                .thenReturn(studyId);

        // when & then
        mockMvc.perform(post("/study")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        Mockito.verify(studyService).createStudy(any(StudyRequestDto.class), any());
    }

    @Test
    @DisplayName("스터디를 삭제할 수 있다.")
    void deleteStudy() throws Exception {
        // given
        Long studyId = 1L;

        // when & then
        mockMvc.perform(delete("/study/{studyId}", studyId))
                .andExpect(status().isOk());

        Mockito.verify(studyService).deleteStudy(anyLong(), any());
    }

    @Test
    @DisplayName("스터디를 수정할 수 있다.")
    void updateStudy() throws Exception {
        // given
        Long studyId = 1L;
        StudyRequestDto requestDto = createStudyRequestDto();

        // when & then
        mockMvc.perform(put("/study/{studyId}", studyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        Mockito.verify(studyService).updateStudy(anyLong(), any(StudyRequestDto.class), any());
    }

    @Test
    @DisplayName("스터디 참여를 요청할 수 있다.")
    void requestJoin() throws Exception {
        // given
        Long studyId = 1L;

        // when & then
        mockMvc.perform(post("/study/{studyId}/join", studyId))
                .andExpect(status().isOk());

        Mockito.verify(studyService).requestJoin(anyLong(), any());
    }

    @Test
    @DisplayName("스터디 참여를 승인할 수 있다.")
    void approveJoin() throws Exception {
        // given
        Long studyId = 1L;
        Long userId = 2L;

        // when & then
        mockMvc.perform(post("/study/{studyId}/approve/{userId}", studyId, userId))
                .andExpect(status().isOk());

        Mockito.verify(studyService).approveJoin(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("스터디 상세 정보를 조회할 수 있다.")
    void getStudyDetail() throws Exception {
        // given
        Long studyId = 1L;
        StudyResponseDto responseDto = createStudyResponseDto();

        Mockito.when(studyService.getStudyDetail(anyLong(), any()))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/study/{studyId}", studyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("아침 기상 스터디"))
                .andExpect(jsonPath("$.description").value("매일 아침 6시에 일어나는 스터디입니다."))
                .andExpect(jsonPath("$.maxParticipants").value(10))
                .andExpect(jsonPath("$.penalty").value(2000));

        Mockito.verify(studyService).getStudyDetail(anyLong(), any());
    }

    @Test
    @DisplayName("모든 스터디 목록을 조회할 수 있다.")
    void getAllStudies() throws Exception {
        // given
        StudyResponseDto study1 = createStudyResponseDto();
        StudyResponseDto study2 = createStudyResponseDto();
        
        Page<StudyResponseDto> studiesPage = new PageImpl<>(List.of(study1, study2), PageRequest.of(0, 10), 2);

        Mockito.when(studyService.getAllStudies(any(), any()))
                .thenReturn(studiesPage);

        // when & then
        mockMvc.perform(get("/study")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        Mockito.verify(studyService).getAllStudies(any(), any());
    }

    @Test
    @DisplayName("내 스터디 목록을 조회할 수 있다.")
    void getMyStudies() throws Exception {
        // given
        StudyResponseDto myStudy = createStudyResponseDto();
        
        Page<StudyResponseDto> myStudiesPage = new PageImpl<>(List.of(myStudy), PageRequest.of(0, 10), 1);

        Mockito.when(studyService.getMyStudies(any(), any()))
                .thenReturn(myStudiesPage);

        // when & then
        mockMvc.perform(get("/study/my")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        Mockito.verify(studyService).getMyStudies(any(), any());
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

    private StudyResponseDto createStudyResponseDto() {
        return StudyResponseDto.builder()
                .id(1L)
                .title("아침 기상 스터디")
                .description("매일 아침 6시에 일어나는 스터디입니다.")
                .wakeUpTime(LocalTime.of(6, 0))
                .studyDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY))
                .verificationLevel(1)
                .maxParticipants(10)
                .currentParticipants(5)
                .penalty(2000)
                .createdBy("테스트사용자")
                .isOwner(false)
                .isParticipant(false)
                .build();
    }
} 