package com.example.wakeupmate.study.controller;

import com.example.wakeupmate.study.dto.StudyRequestDto;
import com.example.wakeupmate.study.dto.StudyResponseDto;
import com.example.wakeupmate.study.service.StudyService;
import com.example.wakeupmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<Long> createStudy(@RequestBody StudyRequestDto requestDto,
                                            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(studyService.createStudy(requestDto, user));
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId, @AuthenticationPrincipal User user) {
        studyService.deleteStudy(studyId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{studyId}")
    public ResponseEntity<Void> updateStudy(@PathVariable Long studyId, @RequestBody StudyRequestDto requestDto, @AuthenticationPrincipal User user) {
        studyService.updateStudy(studyId, requestDto, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{studyId}/join")
    public ResponseEntity<Void> requestJoin(@PathVariable Long studyId, @AuthenticationPrincipal User user) {
        studyService.requestJoin(studyId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{studyId}/approve/{userId}")
    public ResponseEntity<Void> approveJoin(@PathVariable Long studyId, @PathVariable Long userId, @AuthenticationPrincipal User admin) {
        studyService.approveJoin(studyId, userId, admin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto> getStudyDetail(@PathVariable Long studyId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyService.getStudyDetail(studyId, user));
    }

    @GetMapping
    public ResponseEntity<Page<StudyResponseDto>> getAllStudies(Pageable pageable, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyService.getAllStudies(pageable, user));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<StudyResponseDto>> getMyStudies(Pageable pageable, User user) {
        return ResponseEntity.ok(studyService.getMyStudies(pageable, user));
    }
}
