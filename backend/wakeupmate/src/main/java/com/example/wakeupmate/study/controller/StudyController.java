package com.example.wakeupmate.study.controller;


import com.example.wakeupmate.study.domain.Study;
import com.example.wakeupmate.study.service.StudyService;
import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;


@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;
    private final UserService userService;

    // TODO: 스터디 접속 시 인증 상태 리턴
//    @GetMapping("/{studyId}")
//    public ResponseEntity<String> verifyAttendance(@PathVariable Long studyId, @RequestParam Long userId)


}
