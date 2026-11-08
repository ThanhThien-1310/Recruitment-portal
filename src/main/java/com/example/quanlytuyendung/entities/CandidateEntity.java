package com.example.quanlytuyendung.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates")
public class CandidateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String appliedJobTitle;
    private Long jobId;

    // NEW, SCREENING, TECH_INTERVIEW, HR_ROUND, OFFER, HIRED, REJECTED
    private String stage;

    private Double rating = 0.0;
    private String resumeUrl;
    private String resumeLocalPath;

    @Column(columnDefinition = "TEXT")
    private String parsedSkills;

    private Integer experienceYears = 0;

    // Flag for screening knockout: PASSED, FAILED_KNOCKOUT, PENDING
    private String screeningStatus = "PASSED";

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime appliedAt = LocalDateTime.now();

    public CandidateEntity() {}

    public CandidateEntity(String fullName, String email, String phone, String appliedJobTitle, Long jobId, String stage, Double rating, String resumeUrl, String parsedSkills, Integer experienceYears, String screeningStatus) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.appliedJobTitle = appliedJobTitle;
        this.jobId = jobId;
        this.stage = stage;
        this.rating = rating;
        this.resumeUrl = resumeUrl;
        this.parsedSkills = parsedSkills;
        this.experienceYears = experienceYears;
        this.screeningStatus = screeningStatus;
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAppliedJobTitle() { return appliedJobTitle; }
    public void setAppliedJobTitle(String appliedJobTitle) { this.appliedJobTitle = appliedJobTitle; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public String getResumeLocalPath() { return resumeLocalPath; }
    public void setResumeLocalPath(String resumeLocalPath) { this.resumeLocalPath = resumeLocalPath; }

    public String getParsedSkills() { return parsedSkills; }
    public void setParsedSkills(String parsedSkills) { this.parsedSkills = parsedSkills; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getScreeningStatus() { return screeningStatus; }
    public void setScreeningStatus(String screeningStatus) { this.screeningStatus = screeningStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
