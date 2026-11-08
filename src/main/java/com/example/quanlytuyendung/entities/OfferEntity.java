package com.example.quanlytuyendung.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
public class OfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String jobTitle;
    private String department;
    private String baseSalary;
    private String signOnBonus;
    private String startDate;

    // PENDING, APPROVED, REJECTED, ACCEPTED
    private String status = "PENDING";

    private String approvedBy;
    private String pdfFilePath;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    public OfferEntity() {}

    public OfferEntity(Long candidateId, String candidateName, String candidateEmail, String jobTitle, String department, String baseSalary, String signOnBonus, String startDate, String status, String approvedBy) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.jobTitle = jobTitle;
        this.department = department;
        this.baseSalary = baseSalary;
        this.signOnBonus = signOnBonus;
        this.startDate = startDate;
        this.status = status;
        this.approvedBy = approvedBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBaseSalary() { return baseSalary; }
    public void setBaseSalary(String baseSalary) { this.baseSalary = baseSalary; }

    public String getSignOnBonus() { return signOnBonus; }
    public void setSignOnBonus(String signOnBonus) { this.signOnBonus = signOnBonus; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getPdfFilePath() { return pdfFilePath; }
    public void setPdfFilePath(String pdfFilePath) { this.pdfFilePath = pdfFilePath; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
