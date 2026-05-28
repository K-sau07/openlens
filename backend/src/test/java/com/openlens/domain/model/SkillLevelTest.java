package com.openlens.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillLevelTest {

    @Test
    void fromString_returnsCorrectLevel() {
        assertEquals(SkillLevel.BEGINNER, SkillLevel.fromString("beginner"));
        assertEquals(SkillLevel.INTERMEDIATE, SkillLevel.fromString("INTERMEDIATE"));
        assertEquals(SkillLevel.ADVANCED, SkillLevel.fromString("Advanced"));
    }

    @Test
    void fromString_returnsBeginnerForNull() {
        assertEquals(SkillLevel.BEGINNER, SkillLevel.fromString(null));
    }

    @Test
    void fromString_returnsBeginnerForBlank() {
        assertEquals(SkillLevel.BEGINNER, SkillLevel.fromString(""));
        assertEquals(SkillLevel.BEGINNER, SkillLevel.fromString("   "));
    }

    @Test
    void fromString_returnsBeginnerForInvalidInput() {
        assertEquals(SkillLevel.BEGINNER, SkillLevel.fromString("expert"));
        assertEquals(SkillLevel.BEGINNER, SkillLevel.fromString("garbage"));
    }
}
