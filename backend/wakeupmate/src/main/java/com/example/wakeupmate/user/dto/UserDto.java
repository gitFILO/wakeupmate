package com.example.wakeupmate.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private String role;
    private String name;
    private String username;
}
