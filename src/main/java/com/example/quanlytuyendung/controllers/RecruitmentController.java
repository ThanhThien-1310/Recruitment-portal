package com.example.quanlytuyendung.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.quanlytuyendung.entities.*;
import com.example.quanlytuyendung.repositories.*;
import com.example.quanlytuyendung.services.EmailNotificationService;
import com.example.quanlytuyendung.services.PdfOfferService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class RecruitmentController {

    private final JobPostingRepository jobPostingRepository;
    private final CandidateRepository candidateRepository;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final OnboardingRepository onboardingRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final PdfOfferService pdfOfferService;
    private final EmailNotificationService emailNotificationService;
    private final PasswordEncoder passwordEncoder;

    public RecruitmentController(JobPostingRepository jobPostingRepository,
                                 CandidateRepository candidateRepository,
                                 InterviewRepository interviewRepository,
                                 OfferRepository offerRepository,
                                 OnboardingRepository onboardingRepository,
                                 UserRepository userRepository,
                                 AuditLogRepository auditLogRepository,
                                 PdfOfferService pdfOfferService,
                                 EmailNotificationService emailNotificationService,
                                 PasswordEncoder passwordEncoder) {
        this.jobPostingRepository = jobPostingRepository;
        this.candidateRepository = candidateRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.onboardingRepository = onboardingRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.pdfOfferService = pdfOfferService;
        this.emailNotificationService = emailNotificationService;
        this.passwordEncoder = passwordEncoder;
    }

    // Trang chủ quản trị ATS Portal
    @GetMapping({"", "/", "/recruitment", "/recruitment/**"})
    public String recruitmentPortal() {
        return "forward:/recruitment_prototype.html";
    }

    // Trang mô phỏng Mobile ATS App trên khung iPhone
    @GetMapping("/mobile")
    public String mobilePreview() {
        return "forward:/mobile_preview.html";
    }

    // ================= AUTH & RBAC APIS =================
    @ResponseBody
    @PostMapping("/api/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> creds) {
        String username = creds.get("username");
        String password = creds.get("password");

        Map<String, Object> res = new HashMap<>();
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            boolean match = false;

            if (user.getPassword() != null && (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$") || user.getPassword().startsWith("$2y$"))) {
                match = passwordEncoder.matches(password, user.getPassword());
            } else if (user.getPassword() != null && user.getPassword().equals(password)) {
                match = true;
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }

            if (match) {
                res.put("status", "SUCCESS");
                res.put("userId", user.getId());
                res.put("username", user.getUsername());
                res.put("fullName", user.getFullName());
                res.put("role", user.getRole()); // HR_MANAGER, RECRUITER, INTERVIEWER
                res.put("department", user.getDepartment());
                res.put("email", user.getEmail());

                auditLogRepository.save(new AuditLogEntity(user.getUsername(), user.getRole(), "USER_LOGIN", "Auth", "127.0.0.1", "Đăng nhập hệ thống thành công (Xác thực BCrypt)"));
                return res;
            }
        }

        res.put("status", "ERROR");
        res.put("message", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        return res;
    }

    @ResponseBody
    @GetMapping("/api/auth/users")
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // ================= REST APIS DÀNH CHO WEB VÀ MOBILE =================

    // 1. Danh sách tin tuyển dụng từ Database
    @ResponseBody
    @GetMapping("/recruitment/api/jobs")
    public List<JobPostingEntity> apiGetJobs() {
        return jobPostingRepository.findAll();
    }

    // 2. Chi tiết tin tuyển dụng
    @ResponseBody
    @GetMapping("/recruitment/api/jobs/{id}")
    public ResponseEntity<JobPostingEntity> apiGetJobDetail(@PathVariable Long id) {
        return jobPostingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Danh sách ứng viên theo vị trí hoặc toàn bộ từ Database
    @ResponseBody
    @GetMapping("/recruitment/api/candidates")
    public List<CandidateEntity> apiGetCandidates(@RequestParam(required = false) Long jobId) {
        if (jobId != null) {
            return candidateRepository.findByJobId(jobId);
        }
        return candidateRepository.findAll();
    }

    // 4. Chi tiết một ứng viên
    @ResponseBody
    @GetMapping("/recruitment/api/candidates/{id}")
    public ResponseEntity<CandidateEntity> apiGetCandidateDetail(@PathVariable Long id) {
        return candidateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Cập nhật bước tuyển dụng (Kanban Stage Transition)
    @ResponseBody
    @PutMapping("/recruitment/api/candidates/{id}/stage")
    public Map<String, Object> apiUpdateCandidateStage(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStage = payload.get("stage");
        Map<String, Object> res = new HashMap<>();

        Optional<CandidateEntity> candOpt = candidateRepository.findById(id);
        if (candOpt.isPresent()) {
            CandidateEntity cand = candOpt.get();
            String oldStage = cand.getStage();
            cand.setStage(newStage);
            candidateRepository.save(cand);

            auditLogRepository.save(new AuditLogEntity("HR Recruiter", "RECRUITER", "STAGE_CHANGE", "Candidate #" + id, "127.0.0.1", "Chuyển bước từ " + oldStage + " sang " + newStage));

            res.put("status", "SUCCESS");
            res.put("candidateId", id);
            res.put("newStage", newStage);
            res.put("message", "Đã cập nhật trạng thái ứng viên thành công!");
            return res;
        }

        res.put("status", "NOT_FOUND");
        return res;
    }

    // 6. Lịch phỏng vấn từ Database
    @ResponseBody
    @GetMapping("/recruitment/api/interviews")
    public List<InterviewEntity> apiGetInterviews() {
        return interviewRepository.findAll();
    }

    // 7. Tạo lịch phỏng vấn mới & gửi Email mời phỏng vấn tự động kèm file .ics
    @ResponseBody
    @PostMapping("/recruitment/api/interviews")
    public Map<String, Object> apiCreateInterview(@RequestBody Map<String, Object> payload) {
        Long candidateId = payload.get("candidateId") != null ? Long.valueOf(payload.get("candidateId").toString()) : 1L;
        String candidateName = (String) payload.getOrDefault("candidateName", "Ứng viên");
        String candidateEmail = (String) payload.getOrDefault("candidateEmail", "candidate@gmail.com");
        String jobTitle = (String) payload.getOrDefault("jobTitle", "Vị trí tuyển dụng");
        String date = (String) payload.getOrDefault("date", "Hôm nay");
        String time = (String) payload.getOrDefault("time", "14:00 - 15:00");
        String round = (String) payload.getOrDefault("round", "Phỏng vấn Kỹ thuật");
        String interviewer = (String) payload.getOrDefault("interviewer", "Ban Phỏng vấn");
        String format = (String) payload.getOrDefault("format", "Trực tuyến (Google Meet)");
        String meetingLink = (String) payload.getOrDefault("meetingLink", "https://meet.google.com/talentflow-interview");

        InterviewEntity interview = new InterviewEntity(candidateId, candidateName, jobTitle, date, time, round, interviewer, format, meetingLink, "Sắp diễn ra");
        interviewRepository.save(interview);

        // Gửi email tự động
        emailNotificationService.sendInterviewInvitation(candidateEmail, candidateName, jobTitle, date, time, format, meetingLink);

        // Ghi nhận Audit Log
        auditLogRepository.save(new AuditLogEntity("Recruiter", "RECRUITER", "SCHEDULE_INTERVIEW", candidateName, "127.0.0.1", "Lên lịch phỏng vấn vòng " + round + " lúc " + time + " ngày " + date));

        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("interviewId", interview.getId());
        res.put("message", "Lên lịch phỏng vấn thành công và đã gửi thư mời kèm lịch .ics đến email ứng viên!");
        return res;
    }

    // 8. Chấm điểm Scorecard phỏng vấn (1-Click PASS/FAIL & điểm 4 tiêu chí)
    @ResponseBody
    @PostMapping({"/recruitment/api/candidates/{id}/evaluate", "/recruitment/api/interviews/{id}/evaluate"})
    public Map<String, Object> apiSubmitEvaluation(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> payload) {
        Double tech = payload != null && payload.containsKey("technicalScore") ? Double.valueOf(payload.get("technicalScore").toString()) : 4.5;
        Double problem = payload != null && payload.containsKey("problemSolvingScore") ? Double.valueOf(payload.get("problemSolvingScore").toString()) : 4.0;
        Double comm = payload != null && payload.containsKey("communicationScore") ? Double.valueOf(payload.get("communicationScore").toString()) : 4.5;
        Double culture = payload != null && payload.containsKey("culturalFitScore") ? Double.valueOf(payload.get("culturalFitScore").toString()) : 4.5;
        String decision = payload != null && payload.containsKey("decision") ? (String) payload.get("decision") : "PASS";
        String notes = payload != null && payload.containsKey("notes") ? (String) payload.get("notes") : "Đánh giá tốt";

        Optional<InterviewEntity> ivOpt = interviewRepository.findById(id);
        if (ivOpt.isPresent()) {
            InterviewEntity iv = ivOpt.get();
            iv.setTechnicalScore(tech);
            iv.setProblemSolvingScore(problem);
            iv.setCommunicationScore(comm);
            iv.setCulturalFitScore(culture);
            iv.setDecision(decision);
            iv.setNotes(notes);
            iv.setStatus("Hoàn thành");
            interviewRepository.save(iv);

            // Cập nhật rating và stage cho candidate
            Optional<CandidateEntity> candOpt = candidateRepository.findById(iv.getCandidateId());
            if (candOpt.isPresent()) {
                CandidateEntity c = candOpt.get();
                double gpa = (tech + problem + comm + culture) / 4.0;
                c.setRating(Math.round(gpa * 10.0) / 10.0);
                if ("PASS".equalsIgnoreCase(decision)) {
                    c.setStage("OFFER");
                }
                candidateRepository.save(c);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("interviewId", id);
        res.put("decision", decision);
        res.put("message", "Đã lưu phiếu đánh giá Scorecard vào cơ sở dữ liệu!");
        return res;
    }

    // 9. Danh sách phê duyệt yêu cầu (Approvals)
    @ResponseBody
    @GetMapping("/recruitment/api/approvals")
    public List<Map<String, Object>> apiGetApprovals() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<OfferEntity> offers = offerRepository.findByStatus("PENDING");
        for (OfferEntity off : offers) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", off.getId());
            item.put("category", "offer");
            item.put("title", off.getCandidateName());
            item.put("subtitle", off.getJobTitle());
            item.put("tag", "Thư mời nhận việc");
            item.put("highlight", off.getBaseSalary());

            Map<String, String> details = new HashMap<>();
            details.put("Thưởng gia nhập:", off.getSignOnBonus() != null ? off.getSignOnBonus() : "Theo quy chế");
            details.put("Ngày nhận việc:", off.getStartDate() != null ? off.getStartDate() : "Theo thỏa thuận");
            details.put("Phòng ban:", off.getDepartment());
            item.put("details", details);
            item.put("actionTitle", "Duyệt Offer");
            list.add(item);
        }
        return list;
    }

    // 10. Thao tác phê duyệt Offer (Tự động sinh PDF Offer Letter & Gửi Email)
    @ResponseBody
    @PostMapping("/recruitment/api/approvals/{id}/action")
    public Map<String, Object> apiHandleApprovalAction(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload) {
        String action = payload != null && payload.containsKey("action") ? payload.get("action") : "APPROVE";
        Map<String, Object> res = new HashMap<>();

        Optional<OfferEntity> offOpt = offerRepository.findById(id);
        if (offOpt.isPresent()) {
            OfferEntity off = offOpt.get();
            if ("APPROVE".equalsIgnoreCase(action)) {
                off.setStatus("APPROVED");

                // Sinh file PDF Offer chính thức bằng Apache PDFBox
                try {
                    String pdfPath = pdfOfferService.saveOfferPdfToFile(off.getId(), off.getCandidateName(), off.getJobTitle(), off.getDepartment(), off.getBaseSalary(), off.getSignOnBonus(), off.getStartDate());
                    off.setPdfFilePath(pdfPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Gửi email thông báo trúng tuyển
                emailNotificationService.sendOfferNotification(off.getCandidateEmail(), off.getCandidateName(), off.getJobTitle(), off.getBaseSalary(), off.getStartDate());

                // Chuyển candidate sang HIRED / ONBOARDING
                Optional<CandidateEntity> candOpt = candidateRepository.findById(off.getCandidateId());
                if (candOpt.isPresent()) {
                    CandidateEntity c = candOpt.get();
                    c.setStage("HIRED");
                    candidateRepository.save(c);

                    // Tự động tạo onboarding tasks
                    onboardingRepository.save(new OnboardingEntity(c.getId(), c.getFullName(), "Tạo tài khoản email & thiết bị", "IT Support", "Admin", off.getStartDate(), "CHƯA_LÀM"));
                    onboardingRepository.save(new OnboardingEntity(c.getId(), c.getFullName(), "Ký hợp đồng lao động & đóng bảo hiểm", "Nhân sự", "Thu Hà", off.getStartDate(), "CHƯA_LÀM"));
                }
            } else {
                off.setStatus("REJECTED");
            }
            offerRepository.save(off);

            auditLogRepository.save(new AuditLogEntity("HR Manager", "HR_MANAGER", "OFFER_" + action, off.getCandidateName(), "127.0.0.1", "Phê duyệt Thư mời nhận việc: " + action));

            res.put("status", "SUCCESS");
            res.put("offerId", id);
            res.put("action", action);
            res.put("pdfUrl", off.getPdfFilePath());
            res.put("message", "Thao tác " + action + " cho Offer #" + id + " đã được xử lý và xuất PDF Offer Letter thành công!");
            return res;
        }

        res.put("status", "NOT_FOUND");
        return res;
    }

    // 11. Tải file PDF Offer Letter chính thức
    @GetMapping("/recruitment/api/offers/{id}/pdf")
    public ResponseEntity<byte[]> downloadOfferPdf(@PathVariable Long id) {
        Optional<OfferEntity> offOpt = offerRepository.findById(id);
        if (offOpt.isPresent()) {
            OfferEntity off = offOpt.get();
            try {
                byte[] pdfBytes = pdfOfferService.generateOfferLetterBytes(
                        off.getCandidateName(), off.getJobTitle(), off.getDepartment(),
                        off.getBaseSalary(), off.getSignOnBonus(), off.getStartDate()
                );

                String filename = "Offer_Letter_" + off.getCandidateName().replaceAll("\\s+", "_") + ".pdf";
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdfBytes);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    // 12. Xuất báo cáo tuyển dụng ra file CSV / Excel
    @GetMapping("/recruitment/api/export-excel")
    public ResponseEntity<byte[]> exportExcel() {
        StringBuilder csv = new StringBuilder();
        csv.append("\uFEFF"); // UTF-8 BOM
        csv.append("Mã Ứng Viên,Họ và Tên,Vị Trí Tuyển Dụng,Giai Đoạn,Điểm Đánh Giá,Kết Quả Sàng Lọc,Ngày Nộp Đơn\n");

        List<CandidateEntity> candidates = candidateRepository.findAll();
        for (CandidateEntity c : candidates) {
            csv.append("APP-2026-").append(c.getId()).append(",")
               .append("\"").append(c.getFullName()).append("\",")
               .append("\"").append(c.getAppliedJobTitle()).append("\",")
               .append(c.getStage()).append(",")
               .append(c.getRating()).append(",")
               .append(c.getScreeningStatus()).append(",")
               .append(c.getAppliedAt()).append("\n");
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Bao_Cao_Tuyen_Dung_2026.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    // 13. Audit Log gần nhất
    @ResponseBody
    @GetMapping("/recruitment/api/audit-logs")
    public List<AuditLogEntity> getAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    // 14. Lấy nhanh tóm tắt hồ sơ CV ứng viên phục vụ Mobile Bottom Sheet
    @ResponseBody
    @GetMapping("/recruitment/api/candidates/{id}/cv-summary")
    public ResponseEntity<Map<String, Object>> apiGetCandidateCvSummary(@PathVariable Long id) {
        return candidateRepository.findById(id).map(cand -> {
            Map<String, Object> res = new HashMap<>();
            res.put("candidateId", cand.getId());
            res.put("fullName", cand.getFullName());
            res.put("appliedJobTitle", cand.getAppliedJobTitle());
            res.put("experience", cand.getExperienceYears() + " năm kinh nghiệm");
            res.put("skills", cand.getParsedSkills());
            res.put("matchRate", (int)(cand.getRating() * 20));
            res.put("stage", cand.getStage());
            res.put("notes", cand.getNotes());
            res.put("pdfUrl", cand.getResumeUrl());
            return ResponseEntity.ok(res);
        }).orElse(ResponseEntity.notFound().build());
    }
}
