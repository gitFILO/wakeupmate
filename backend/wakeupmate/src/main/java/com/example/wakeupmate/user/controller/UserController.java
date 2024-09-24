package com.example.wakeupmate.user.controller;

import com.example.wakeupmate.user.domain.User;
import com.example.wakeupmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

}
