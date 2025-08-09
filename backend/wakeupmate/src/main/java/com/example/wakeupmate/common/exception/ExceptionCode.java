package com.example.wakeupmate.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {
    // 공통 예외
    INVALID_REQUEST(1000, "유효하지 않은 요청입니다."),
    
    // 스터디 관련
    STUDY_NOT_FOUND(2001, "스터디가 존재하지 않습니다."),
    STUDY_OWNER_ONLY(2002, "방장만 이 작업을 수행할 수 있습니다."),
    CANNOT_JOIN_OWN_STUDY(2003, "자신이 만든 스터디에는 참여할 수 없습니다."),
    ALREADY_PARTICIPATING(2004, "이미 참여 중인 스터디입니다."),
    ALREADY_REQUESTED(2005, "이미 참여 요청한 스터디입니다."),
    STUDY_CAPACITY_FULL(2006, "스터디 정원이 가득찼습니다."),
    JOIN_REQUEST_NOT_FOUND(2007, "참여 요청을 찾을 수 없습니다."),
    ALREADY_APPROVED(2008, "이미 승인된 사용자입니다."),
    STUDY_PARTICIPANT_ONLY(2009, "스터디 참여자만 이 작업을 수행할 수 있습니다."),
    
    // 사용자 관련
    USER_NOT_FOUND(3001, "사용자를 찾을 수 없습니다."),
    
    // 장소 관련
    PLACE_NOT_FOUND(4001, "장소를 찾을 수 없습니다."),
    PLACE_OWNER_ONLY(4002, "본인의 장소만 설정할 수 있습니다."),
    PLACE_NOT_SET_FOR_ADMIN(4003, "관리자는 먼저 스터디 장소를 설정해주세요."),
    PLACE_NOT_SET_FOR_STUDY(4004, "스터디에서 사용할 장소를 먼저 설정해주세요."),
    
    // 위치 관련
    LOCATION_ACCESS_DENIED(5001, "스터디 참여자만 위치를 업데이트할 수 있습니다."),
    LOCATION_QUERY_ACCESS_DENIED(5002, "스터디 참여자만 위치 정보를 조회할 수 있습니다."),
    
    // JWT 관련
    INVALID_JWT_TOKEN(6001, "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(6002, "만료된 JWT 토큰입니다."),
    FAILED_TO_VALIDATE_TOKEN(6003, "토큰 검증에 실패했습니다."),
    
    // 서버
    INTERNAL_SERVER_ERROR(9000, "서버 내부 오류가 발생했습니다.");

    private final int code;
    private final String message;
}