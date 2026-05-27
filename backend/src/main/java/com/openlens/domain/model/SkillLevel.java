package com.openlens.domain.model;

public enum SkillLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    public static SkillLevel fromString(String raw) {
        if (raw == null || raw.isBlank()) return BEGINNER;
        try {
            return valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BEGINNER;
        }
    }
}
