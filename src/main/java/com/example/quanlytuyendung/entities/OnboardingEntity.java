package com.example.quanlytuyendung.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "onboarding_tasks")
public class OnboardingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;
    private String candidateName;
    private String taskTitle;
    private String department;
    private String assignee;
    private String dueDate;
    private String status; // CHƯA_LÀM, ĐANG_LÀM, HOÀN_THÀNH

    public OnboardingEntity() {}

    public OnboardingEntity(Long candidateId, String candidateName, String taskTitle, String department, String assignee, String dueDate, String status) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.taskTitle = taskTitle;
        this.department = department;
        this.assignee = assignee;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
