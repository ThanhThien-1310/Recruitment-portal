package com.example.quanlytuyendung.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_schedules")
public class InterviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;
    private String candidateName;
    private String jobTitle;
    private String date;
    private String time;
    private String round;
    private String interviewer;
    private String format; // Online (Google Meet) or Offline
    private String meetingLink;
    private String status; // Sắp diễn ra, Hoàn thành, Đã hủy

    // Scores
    private Double technicalScore;
    private Double problemSolvingScore;
    private Double communicationScore;
    private Double culturalFitScore;
    private String decision; // PASS, DENY, PENDING

    @Column(columnDefinition = "TEXT")
    private String notes;

    public InterviewEntity() {}

    public InterviewEntity(Long candidateId, String candidateName, String jobTitle, String date, String time, String round, String interviewer, String format, String meetingLink, String status) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.jobTitle = jobTitle;
        this.date = date;
        this.time = time;
        this.round = round;
        this.interviewer = interviewer;
        this.format = format;
        this.meetingLink = meetingLink;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }

    public String getInterviewer() { return interviewer; }
    public void setInterviewer(String interviewer) { this.interviewer = interviewer; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(Double technicalScore) { this.technicalScore = technicalScore; }

    public Double getProblemSolvingScore() { return problemSolvingScore; }
    public void setProblemSolvingScore(Double problemSolvingScore) { this.problemSolvingScore = problemSolvingScore; }

    public Double getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Double communicationScore) { this.communicationScore = communicationScore; }

    public Double getCulturalFitScore() { return culturalFitScore; }
    public void setCulturalFitScore(Double culturalFitScore) { this.culturalFitScore = culturalFitScore; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
