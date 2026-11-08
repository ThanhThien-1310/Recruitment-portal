package com.example.quanlytuyendung.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "job_postings")
public class JobPostingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String department;
    private String location;
    private String type;
    private String salary;
    private Integer quantity;
    private String deadline;
    private String status;
    private Integer applicantCount = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    // Minimum required experience years for auto knock-out check
    private Integer minExperienceYears = 0;

    public JobPostingEntity() {}

    public JobPostingEntity(String title, String department, String location, String type, String salary, Integer quantity, String deadline, String status, Integer applicantCount, String description, String requirements, String benefits, Integer minExperienceYears) {
        this.title = title;
        this.department = department;
        this.location = location;
        this.type = type;
        this.salary = salary;
        this.quantity = quantity;
        this.deadline = deadline;
        this.status = status;
        this.applicantCount = applicantCount;
        this.description = description;
        this.requirements = requirements;
        this.benefits = benefits;
        this.minExperienceYears = minExperienceYears;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getApplicantCount() { return applicantCount; }
    public void setApplicantCount(Integer applicantCount) { this.applicantCount = applicantCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public Integer getMinExperienceYears() { return minExperienceYears; }
    public void setMinExperienceYears(Integer minExperienceYears) { this.minExperienceYears = minExperienceYears; }
}
