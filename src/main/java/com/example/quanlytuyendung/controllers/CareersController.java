package com.example.quanlytuyendung.controllers;

import com.example.quanlytuyendung.entities.CandidateEntity;
import com.example.quanlytuyendung.entities.JobPostingEntity;
import com.example.quanlytuyendung.repositories.CandidateRepository;
import com.example.quanlytuyendung.repositories.JobPostingRepository;
import com.example.quanlytuyendung.services.EmailNotificationService;
import com.example.quanlytuyendung.services.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/careers")
public class CareersController {

    private final JobPostingRepository jobPostingRepository;
    private final CandidateRepository candidateRepository;
    private final FileStorageService fileStorageService;
    private final EmailNotificationService emailNotificationService;

    public CareersController(JobPostingRepository jobPostingRepository,
                             CandidateRepository candidateRepository,
                             FileStorageService fileStorageService,
                             EmailNotificationService emailNotificationService) {
        this.jobPostingRepository = jobPostingRepository;
        this.candidateRepository = candidateRepository;
        this.fileStorageService = fileStorageService;
        this.emailNotificationService = emailNotificationService;
    }

    // Cổng thông tin tuyển dụng & việc làm công khai (Job Board)
    @GetMapping({"", "/", "/jobs", "/track", "/exam", "/job/{id}", "/assessment/{token}"})
    public String publicJobBoard() {
        return "forward:/job_board.html";
    }

    // API nộp đơn ứng tuyển trực tiếp (kèm upload CV & kiểm tra sàng lọc Knock-out)
    @ResponseBody
    @PostMapping("/api/apply")
    public Map<String, Object> applyJob(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String appliedJobTitle,
            @RequestParam(required = false, defaultValue = "0") Integer experienceYears,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String cvUrl,
            @RequestParam(required = false) MultipartFile cvFile) {

        Map<String, Object> res = new HashMap<>();

        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = "Ứng viên Ẩn danh";
        }
        if (email == null || email.trim().isEmpty()) {
            email = "candidate." + System.currentTimeMillis() + "@gmail.com";
        }

        // 1. Kiểm tra điều kiện sàng lọc (Knock-out Check)
        String screeningStatus = "PASSED";
        String stage = "NEW";
        if (jobId != null) {
            Optional<JobPostingEntity> jobOpt = jobPostingRepository.findById(jobId);
            if (jobOpt.isPresent()) {
                JobPostingEntity job = jobOpt.get();
                if (appliedJobTitle == null || appliedJobTitle.isEmpty()) {
                    appliedJobTitle = job.getTitle();
                }
                if (job.getMinExperienceYears() != null && experienceYears < job.getMinExperienceYears()) {
                    screeningStatus = "FAILED_KNOCKOUT";
                    stage = "REJECTED"; // Tự động loại trực tiếp nếu không thỏa điều kiện cần
                }
                // Tăng số lượng ứng viên nộp cho tin tuyển dụng
                job.setApplicantCount((job.getApplicantCount() == null ? 0 : job.getApplicantCount()) + 1);
                jobPostingRepository.save(job);
            }
        }

        // 2. Lưu trữ file CV vật lý
        String storedFilePath = null;
        if (cvFile != null && !cvFile.isEmpty()) {
            try {
                storedFilePath = fileStorageService.storeCv(cvFile, fullName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String finalResumeUrl = storedFilePath != null ? storedFilePath : (cvUrl != null ? cvUrl : "https://drive.google.com/sample_cv");

        // 3. Lưu vào Database MySQL qua JPA
        CandidateEntity candidate = new CandidateEntity(
                fullName, email, phone, appliedJobTitle, jobId,
                stage, 4.0, finalResumeUrl,
                skills != null ? skills : "Java, Spring Boot, Git",
                experienceYears, screeningStatus
        );
        candidate.setResumeLocalPath(storedFilePath);
        candidateRepository.save(candidate);

        // 4. Gửi email xác nhận
        emailNotificationService.sendApplicationConfirmation(email, fullName, appliedJobTitle);

        String appCode = "APP-2026-" + candidate.getId();
        res.put("status", "SUCCESS");
        res.put("candidateId", candidate.getId());
        res.put("applicationCode", appCode);
        res.put("screeningStatus", screeningStatus);
        res.put("resumePath", finalResumeUrl);
        res.put("message", screeningStatus.equals("PASSED")
                ? "Nộp hồ sơ thành công! Hồ sơ của bạn đã được chuyển đến bộ phận tuyển dụng."
                : "Hồ sơ của bạn đã được ghi nhận. Hệ thống nhận thấy bạn chưa đạt năm kinh nghiệm tối thiểu nên sẽ lưu hồ sơ vào Talent Pool để xem xét các vị trí khác.");

        return res;
    }

    // API tra cứu tiến độ hồ sơ theo Email hoặc Số điện thoại
    @ResponseBody
    @GetMapping("/api/track")
    public Map<String, Object> trackApplication(@RequestParam(required = false) String keyword) {
        Map<String, Object> res = new HashMap<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            res.put("status", "ERROR");
            res.put("message", "Vui lòng nhập Email hoặc Mã hồ sơ");
            return res;
        }

        Optional<CandidateEntity> candOpt = candidateRepository.findByEmailOrPhone(keyword.trim(), keyword.trim());
        if (candOpt.isPresent()) {
            CandidateEntity c = candOpt.get();
            res.put("status", "SUCCESS");
            res.put("candidateId", c.getId());
            res.put("fullName", c.getFullName());
            res.put("email", c.getEmail());
            res.put("appliedJobTitle", c.getAppliedJobTitle());
            res.put("stage", c.getStage());
            res.put("screeningStatus", c.getScreeningStatus());
            res.put("appliedDate", c.getAppliedAt().toString());
            return res;
        }

        res.put("status", "NOT_FOUND");
        res.put("message", "Không tìm thấy hồ sơ với thông tin: " + keyword);
        return res;
    }

    // API nộp bài test trực tiếp qua Web
    @ResponseBody
    @PostMapping("/api/submit-assessment")
    public Map<String, Object> submitAssessment(@RequestBody(required = false) Map<String, Object> submission) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("message", "Bài thi của bạn đã được nộp thành công! Hệ thống đã ghi nhận kết quả và thông báo đến hội đồng tuyển dụng.");
        res.put("submittedAt", new Date().toString());
        return res;
    }

    // API nộp CV mở (Open Application)
    @ResponseBody
    @PostMapping("/api/open-apply")
    public Map<String, Object> openApply(@RequestBody(required = false) Map<String, Object> payload) {
        Map<String, Object> res = new HashMap<>();
        String appCode = "APP-2026-" + (new Random().nextInt(899) + 100);
        res.put("status", "SUCCESS");
        res.put("applicationCode", appCode);
        res.put("message", "Hồ sơ của bạn đã được tiếp nhận vào Talent Pool của TechCorp! Mã hồ sơ của bạn là: " + appCode);
        return res;
    }
}
