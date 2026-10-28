package com.example.quanlytuyendung.dtos;

import java.util.List;

public record ParsedResumeResponse(
        String fullName,
        String email,
        String phone,
        List<String> skills,
        Double experienceYears,
        String education,
        String summary,
        String suggestedRole,
        String sourceEngine,
        String rawTextPreview
) {}
