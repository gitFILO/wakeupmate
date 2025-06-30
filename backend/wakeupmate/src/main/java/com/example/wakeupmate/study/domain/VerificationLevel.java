package com.example.wakeupmate.study.domain;

public enum VerificationLevel {
    ANYWHERE, NOT_HOME, SPECIFIC_PLACE_ONLY;

    public static VerificationLevel of(int verificationLevel) {
        return switch (verificationLevel) {
            case 0 -> ANYWHERE;
            case 1 -> NOT_HOME;
            case 2 -> SPECIFIC_PLACE_ONLY;
            default -> throw new IllegalArgumentException("Unknown verification level: " + verificationLevel);
        };
    }
}