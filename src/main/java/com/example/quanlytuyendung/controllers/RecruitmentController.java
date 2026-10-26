package com.example.quanlytuyendung.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class RecruitmentController {

    // ================= DATA STRUCTURES =================
    public record Requisition(
            Long id,
            String code, // RQ-2026-001
            String title,
            String department,
            int currentHired,
            int totalTarget,
            String priority, // Cao, Trung bình, Thấp
            String priorityBadgeClass, // badge-danger, badge-warning, badge-info
            String status, // Đang tuyển, Chờ duyệt, Nháp, Đã đóng
            String statusBadgeClass,
            String recruiter,
            String deadline,
            String salaryRange
    ) {
        public String progressText() { return currentHired + " / " + totalTarget; }
        public int progressPercent() { return totalTarget > 0 ? (int)((currentHired * 100.0) / totalTarget) : 0; }
    }

    public record JobPosting(
            Long id,
            String title,
            String department,
            String location,
            String type, // Toàn thời gian, Bán thời gian, Thực tập
            String salary,
            int quantity,
            String deadline,
            String status, // Đang tuyển, Tạm dừng, Đã đóng
            int applicantCount,
            String description,
            String requirements,
            String benefits
    ) {}

    public record Candidate(
            Long id,
            String applicationCode, // APP-2026-101
            String fullName,
            String email,
            String phone,
            String appliedJobTitle,
            Long jobId,
            String stage, // NEW, SCREENING, ASSESSMENT, TECH_INTERVIEW, HR_INTERVIEW, OFFER, HIRED, REJECTED
            String experience,
            String education,
            List<String> skills,
            double rating,
            String source, // TopCV, LinkedIn, ITviec, Nội bộ, Website
            String applyDate,
            String avatarUrl,
            String interviewTime,
            String interviewer,
            String notes,
            String testScore, // e.g. "85/100"
            String testStatus // CHƯA_GIAO, ĐÃ_GIAO, ĐÃ_NỘP_WEB, ĐÃ_NỘP_EMAIL, ĐÃ_CHẤM
    ) {
        public String getStageText() {
            return switch (stage) {
                case "NEW" -> "Mới ứng tuyển";
                case "SCREENING" -> "Đang sơ loại CV";
                case "ASSESSMENT" -> "Đang làm bài test";
                case "TECH_INTERVIEW" -> "Phỏng vấn Kỹ thuật";
                case "HR_INTERVIEW" -> "Phỏng vấn HR";
                case "OFFER" -> "Thư mời nhận việc (Offer)";
                case "HIRED" -> "Đã nhận việc (Onboarding)";
                case "REJECTED" -> "Chưa phù hợp";
                default -> stage;
            };
        }

        public String getStageBadgeClass() {
            return switch (stage) {
                case "NEW" -> "badge-info";
                case "SCREENING" -> "badge-warning";
                case "ASSESSMENT" -> "badge-purple";
                case "TECH_INTERVIEW", "HR_INTERVIEW" -> "badge-primary";
                case "OFFER" -> "badge-warning";
                case "HIRED" -> "badge-success";
                case "REJECTED" -> "badge-danger";
                default -> "badge-secondary";
            };
        }

        // Aliases for template bindings
        public String name() { return fullName; }
        public String position() { return appliedJobTitle; }
        public String status() { return getStageText(); }
        public String statusBadgeClass() { return getStageBadgeClass(); }
    }

    public record InterviewSchedule(
            Long id,
            String candidateName,
            String jobTitle,
            String date,
            String time,
            String round, // Phỏng vấn Kỹ thuật, Phỏng vấn HR, Test năng lực
            String interviewer,
            String format, // Trực tiếp (P.302), Online (Google Meet)
            String meetingLink,
            String status, // Sắp diễn ra, Hoàn thành, Đã hủy
            double technicalScore,
            double problemSolvingScore,
            double communicationScore,
            double culturalFitScore,
            String decision // PASS, CONSIDER, FAIL
    ) {}

    public record AssessmentTest(
            Long id,
            String title,
            String jobCategory,
            int durationMinutes,
            int totalQuestions,
            String level, // Fresher, Junior, Senior
            String description
    ) {}

    public record OfferRecord(
            Long id,
            String candidateName,
            String jobTitle,
            String salaryGross,
            String bonusInfo,
            String startDate,
            String deadlineResponse,
            String approvalStatus, // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED
            String candidateResponse, // SENT, ACCEPTED, DECLINED
            String approvedBy
    ) {}

    public record OnboardingTask(
            Long id,
            String candidateName,
            String jobTitle,
            String startDate,
            String mentor,
            int progressPercent,
            List<String> completedTasks,
            List<String> pendingTasks,
            String status // ĐANG_TIẾP_NHẬN, HOÀN_TẤT
    ) {}

    public record AuditLog(
            Long id,
            String timestamp,
            String actorName,
            String actorRole,
            String action,
            String detail
    ) {}

    // ================= MOCK DATA GENERATORS =================
    public List<Requisition> getMockRequisitions() {
        return List.of(
            new Requisition(1L, "RQ-2026-001", "Senior Java Backend Engineer", "Phòng Kỹ thuật & Công nghệ", 0, 2, "Cao", "badge-danger", "Đang tuyển", "badge-success", "Lan (Recruiter)", "15/11/2026", "25 - 35 Triệu"),
            new Requisition(2L, "RQ-2026-002", "Flutter Mobile Developer", "Phòng Kỹ thuật & Công nghệ", 1, 3, "Cao", "badge-danger", "Đang tuyển", "badge-success", "Minh Hưng (HR Lead)", "20/11/2026", "18 - 28 Triệu"),
            new Requisition(3L, "RQ-2026-003", "UI/UX Product Designer", "Phòng Sản phẩm & Thiết kế", 0, 1, "Trung bình", "badge-warning", "Đang tuyển", "badge-success", "Thái Tuấn (Interviewer Lead)", "10/11/2026", "16 - 24 Triệu"),
            new Requisition(4L, "RQ-2026-004", "Chuyên viên Tuyển dụng & Đào tạo", "Phòng Nhân sự", 1, 1, "Trung bình", "badge-warning", "Đã tuyển đủ", "badge-info", "Nguyễn Thu Hà (HR Manager)", "05/11/2026", "12 - 18 Triệu"),
            new Requisition(5L, "RQ-2026-005", "Thực tập sinh Lập trình Web", "Phòng Kỹ thuật & Công nghệ", 2, 5, "Thấp", "badge-info", "Đang tuyển", "badge-success", "Thảo (Recruiter)", "30/11/2026", "4 - 6 Triệu")
        );
    }

    public List<JobPosting> getMockJobs() {
        return List.of(
            new JobPosting(
                1L, "Senior Java Backend Engineer", "Phòng Kỹ thuật & Công nghệ",
                "Hà Nội / Hybrid", "Toàn thời gian", "25 - 35 Triệu VNĐ", 2, "15/11/2026", "Đang tuyển", 28,
                "Phát triển hệ thống microservices backend hiệu năng cao, tối ưu hóa cơ sở dữ liệu lớn và thiết kế kiến trúc hệ thống.",
                "Tối thiểu 3 năm kinh nghiệm với Java, Spring Boot, MySQL/PostgreSQL, Redis, Kafka, Docker.",
                "Thưởng tháng 13 + KPIs, bảo hiểm sức khỏe quốc tế, cấp Macbook Pro M3."
            ),
            new JobPosting(
                2L, "Flutter Mobile Developer", "Phòng Kỹ thuật & Công nghệ",
                "Hà Nội / Remote", "Toàn thời gian", "18 - 28 Triệu VNĐ", 3, "20/11/2026", "Đang tuyển", 35,
                "Xây dựng ứng dụng mobile đa nền tảng (iOS & Android) phục vụ hàng trăm nghìn người dùng, tích hợp REST API.",
                "Tối thiểu 2 năm kinh nghiệm Flutter/Dart, hiểu sâu về State Management (Bloc/Provider).",
                "Môi trường Agile năng động, phụ cấp cơm trưa + gửi xe, đào tạo nâng cao chuyên môn."
            ),
            new JobPosting(
                3L, "UI/UX Product Designer", "Phòng Sản phẩm & Thiết kế",
                "TP. Hồ Chí Minh", "Toàn thời gian", "16 - 24 Triệu VNĐ", 1, "10/11/2026", "Đang tuyển", 19,
                "Thiết kế Design System, User Journey và Wireframe/Prototype trực quan cho Web và Mobile.",
                "Sử dụng thành thạo Figma, tư duy User-Centric, có portfolio dự án ấn tượng.",
                "Màn hình 4K, khám sức khỏe định kỳ, thưởng các dịp lễ tết."
            ),
            new JobPosting(
                4L, "Chuyên viên Tuyển dụng & Đào tạo (HR Recruiter)", "Phòng Nhân sự",
                "Hà Nội", "Toàn thời gian", "12 - 18 Triệu VNĐ", 1, "05/11/2026", "Đang tuyển", 14,
                "Chịu trách nhiệm toàn bộ quy trình sourcing, săn đón nhân tài công nghệ và phỏng vấn sơ loại.",
                "Tối thiểu 1 năm kinh nghiệm IT Recruitment, mạng lưới ứng viên công nghệ tốt.",
                "Hoa hồng tuyển dụng hấp dẫn, làm việc từ thứ 2 đến thứ 6, du lịch hàng năm."
            ),
            new JobPosting(
                5L, "Thực tập sinh Lập trình Web (Spring Boot & Vue/React)", "Phòng Kỹ thuật & Công nghệ",
                "Hà Nội", "Thực tập", "4 - 6 Triệu VNĐ", 5, "30/11/2026", "Đang tuyển", 52,
                "Tham gia trực tiếp vào các module thực tế của dự án, được Mentor 1-1 chỉ dẫn chi tiết.",
                "Sinh viên năm 3, 4 hoặc mới tốt nghiệp CNTT, nắm vững căn bản OOP, Java Core, SQL.",
                "Hỗ trợ dấu thực tập và đồ án tốt nghiệp, phụ cấp ăn trưa."
            )
        );
    }

    public List<Candidate> getMockCandidates() {
        return List.of(
            new Candidate(
                101L, "APP-2026-101", "Nguyễn Văn An", "an.nguyen@gmail.com", "0912 345 678",
                "Senior Java Backend Engineer", 1L, "TECH_INTERVIEW",
                "4.5 năm kinh nghiệm", "ĐH Bách Khoa Hà Nội (CNTT)",
                List.of("Java", "Spring Boot", "Microservices", "Kafka", "Docker"),
                4.8, "TopCV", "24/10/2026", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                "26/10/2026 09:30", "Anh Tuấn (Tech Lead)",
                "Ứng viên có kiến thức hệ thống phân tán và tối ưu database rất vững. Thái độ tích cực.",
                "92/100 (Bài test Java)", "ĐÃ_CHẤM"
            ),
            new Candidate(
                102L, "APP-2026-102", "Trần Thị Mai", "mai.tran@outlook.com", "0987 654 321",
                "Flutter Mobile Developer", 2L, "TECH_INTERVIEW",
                "2.5 năm kinh nghiệm", "ĐH Công nghệ - ĐHQGHN",
                List.of("Flutter", "Dart", "Bloc", "RESTful API", "Firebase"),
                4.5, "LinkedIn", "23/10/2026", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                "26/10/2026 14:00", "Chị Linh (HR) & Anh Hưng (Mobile Lead)",
                "UI làm việc rất cẩn thận, hiểu sâu Bloc Pattern, đã từng phát hành 2 app lên Play Store.",
                "88/100 (Bài test Flutter)", "ĐÃ_CHẤM"
            ),
            new Candidate(
                103L, "APP-2026-103", "Lê Hoàng Nam", "nam.lehoang@gmail.com", "0905 112 233",
                "UI/UX Product Designer", 3L, "HR_INTERVIEW",
                "3 năm kinh nghiệm", "ĐH Kiến Trúc TP.HCM",
                List.of("Figma", "Design System", "User Research", "Prototyping"),
                4.7, "ITviec", "21/10/2026", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                "26/10/2026 15:30", "Anh Nam (Product Manager)",
                "Portfolio rất thẩm mỹ, tư duy sản phẩm định hướng người dùng rõ ràng.",
                "Nộp bài qua Email Reply", "ĐÃ_NỘP_EMAIL"
            ),
            new Candidate(
                104L, "APP-2026-104", "Phạm Quốc Bảo", "bao.pham@gmail.com", "0934 998 877",
                "Senior Java Backend Engineer", 1L, "OFFER",
                "5 năm kinh nghiệm", "Học viện CNBCVT",
                List.of("Java", "Spring Cloud", "PostgreSQL", "Kubernetes", "AWS"),
                4.9, "Nội bộ giới thiệu", "18/10/2026", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                "Đã phỏng vấn xong", "HĐ Tuyển dụng",
                "Đã gửi offer mức 32M, ứng viên đang xem xét phản hồi trước ngày 28/10.",
                "95/100", "ĐÃ_CHẤM"
            ),
            new Candidate(
                105L, "APP-2026-105", "Đỗ Hải Yến", "haiyen.do@gmail.com", "0978 221 144",
                "Chuyên viên Tuyển dụng & Đào tạo", 4L, "HIRED",
                "2 năm kinh nghiệm", "ĐH Kinh Tế Quốc Dân",
                List.of("IT Sourcing", "Interviewing", "Headhunting", "English"),
                4.6, "LinkedIn", "15/10/2026", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
                "Đã nhận việc", "Trưởng phòng HR",
                "Đã hoàn thành thủ tục Onboarding. Bắt đầu làm việc từ ngày 01/11/2026.",
                "85/100", "ĐÃ_CHẤM"
            ),
            new Candidate(
                106L, "APP-2026-106", "Vũ Đức Thắng", "thang.vu@gmail.com", "0918 887 766",
                "Thực tập sinh Lập trình Web", 5L, "ASSESSMENT",
                "Mới tốt nghiệp", "ĐH Giao Thông Vận Tải",
                List.of("Java", "OOP", "MySQL", "HTML/CSS", "Git"),
                4.0, "TopCV", "25/10/2026", "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150",
                "Chờ kết quả test", "Chị Thảo (HR)",
                "GPA 3.4/4.0, đã gửi link làm bài test thuật toán trực tuyến hạn 27/10.",
                "Đang làm bài", "ĐÃ_GIAO"
            ),
            new Candidate(
                107L, "APP-2026-107", "Bùi Minh Trang", "trang.bui@gmail.com", "0945 667 788",
                "Flutter Mobile Developer", 2L, "NEW",
                "1.5 năm kinh nghiệm", "ĐH Bách Khoa Đà Nẵng",
                List.of("Flutter", "Dart", "Provider", "REST API"),
                3.8, "Website công ty", "26/10/2026", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                "Chưa liên hệ", "HR Tuyển dụng",
                "Hồ sơ mới nộp sáng nay qua cổng tuyển dụng Careers Portal.",
                "Chưa giao", "CHƯA_GIAO"
            )
        );
    }

    public List<InterviewSchedule> getMockInterviews() {
        return List.of(
            new InterviewSchedule(
                1L, "Nguyễn Văn An", "Senior Java Backend Engineer",
                "Hôm nay (26/10/2026)", "09:30 - 10:30",
                "Phỏng vấn Kỹ thuật (Vòng 2)", "Anh Tuấn (Tech Lead), Anh Minh (Senior Dev)",
                "Online (Google Meet)", "https://meet.google.com/abc-xyz-ats", "Sắp diễn ra",
                4.8, 4.9, 4.5, 4.8, "PASS"
            ),
            new InterviewSchedule(
                2L, "Trần Thị Mai", "Flutter Mobile Developer",
                "Hôm nay (26/10/2026)", "14:00 - 15:00",
                "Phỏng vấn Kỹ thuật & Live Coding", "Chị Linh (HR) & Anh Hưng (Mobile Lead)",
                "Trực tiếp tại Phòng họp 302", "-", "Sắp diễn ra",
                4.6, 4.4, 4.7, 4.5, "PASS"
            ),
            new InterviewSchedule(
                3L, "Lê Hoàng Nam", "UI/UX Product Designer",
                "Hôm nay (26/10/2026)", "15:30 - 16:30",
                "Phỏng vấn Portfolio & Văn hóa", "Anh Nam (Product Manager)",
                "Online (Google Meet)", "https://meet.google.com/des-room-ats", "Sắp diễn ra",
                4.7, 4.8, 4.6, 4.7, "PASS"
            )
        );
    }

    public List<AssessmentTest> getMockAssessments() {
        return List.of(
            new AssessmentTest(1L, "Bài Test Năng Lực Java Core & Spring Boot", "Phòng Kỹ thuật & Công nghệ", 60, 25, "Senior", "Đánh giá kiến thức OOP, Multithreading, Spring Core, JPA, Microservices và SQL Tuning."),
            new AssessmentTest(2L, "Bài Test Lập Trình Mobile Flutter & Dart", "Phòng Kỹ thuật & Công nghệ", 45, 20, "Junior / Mid", "Đánh giá hiểu biết State Management (Bloc/Provider), Widget Lifecycle, REST API."),
            new AssessmentTest(3L, "Đánh Giá Tư Duy Sản Phẩm & UI/UX Design System", "Phòng Sản phẩm", 60, 15, "Mid", "Thiết kế component trên Figma, phân tích luồng trải nghiệm người dùng User Journey."),
            new AssessmentTest(4L, "Kiểm Tra Tiếng Anh Chuyên Ngành IT & Giao Tiếp", "Chung", 30, 30, "Mọi cấp bậc", "Trắc nghiệm ngữ pháp, từ vựng kỹ thuật và đọc hiểu tài liệu phần mềm.")
        );
    }

    public List<OfferRecord> getMockOffers() {
        return List.of(
            new OfferRecord(1L, "Phạm Quốc Bảo", "Senior Java Backend Engineer", "32.000.000 VNĐ", "Thưởng KPIs + Thưởng gia nhập 10M", "01/11/2026", "28/10/2026", "APPROVED", "SENT", "Lê Văn Thành (Giám đốc Khối CN)"),
            new OfferRecord(2L, "Lê Hoàng Nam", "UI/UX Product Designer", "22.000.000 VNĐ", "Thưởng tháng 13 + Cấp Macbook", "05/11/2026", "30/10/2026", "PENDING_APPROVAL", "Chờ duyệt", "Chờ Giám đốc phê duyệt"),
            new OfferRecord(3L, "Đỗ Hải Yến", "Chuyên viên Tuyển dụng & Đào tạo", "16.000.000 VNĐ", "Hoa hồng tuyển dụng theo quý", "01/11/2026", "20/10/2026", "APPROVED", "ACCEPTED", "Nguyễn Thu Hà (HR Director)")
        );
    }

    public List<OnboardingTask> getMockOnboarding() {
        return List.of(
            new OnboardingTask(
                1L, "Đỗ Hải Yến", "Chuyên viên Tuyển dụng & Đào tạo", "01/11/2026", "Chị Thu Hà (HR Manager)", 75,
                List.of("Ký hợp đồng lao động & hồ sơ nhân sự", "Tạo email công ty @techcorp.vn", "Cấp thẻ nhân viên & bàn làm việc"),
                List.of("Cấp quyền truy cập hệ thống TalentFlow ATS", "Buổi Orientation văn hóa công ty vào ngày 01/11"),
                "ĐANG_TIẾP_NHẬN"
            ),
            new OnboardingTask(
                2L, "Phạm Quốc Bảo", "Senior Java Backend Engineer", "01/11/2026", "Anh Tuấn (Tech Lead)", 40,
                List.of("Ký hợp đồng thử việc", "Chuẩn bị Macbook Pro M3"),
                List.of("Cấp tài khoản GitHub Enterprise, Jira, Slack", "Bàn giao tài liệu kiến trúc hệ thống", "Tham gia buổi Kickoff Sprint"),
                "ĐANG_TIẾP_NHẬN"
            )
        );
    }

    public List<AuditLog> getMockAuditLogs() {
        return List.of(
            new AuditLog(1L, "26/10/2026 10:45", "Anh Tuấn", "Tech Lead", "Đánh giá Scorecard", "Chấm điểm vòng PV Kỹ thuật cho ứng viên Nguyễn Văn An (Điểm 4.8/5)"),
            new AuditLog(2L, "26/10/2026 09:15", "Chị Thảo", "Recruiter", "Giao bài Test năng lực", "Tạo và gửi link làm bài test bảo mật cho Vũ Đức Thắng (Hạn 27/10)"),
            new AuditLog(3L, "25/10/2026 16:30", "Minh Hưng", "HR Lead", "Phê duyệt Thư mời Offer", "Duyệt bản thảo Offer 32M cho ứng viên Phạm Quốc Bảo"),
            new AuditLog(4L, "25/10/2026 14:00", "Hệ thống Webhook", "System", "Nhận bài Test qua Email", "Inbound Webhook tự động ghi nhận bài làm từ nam.lehoang@gmail.com"),
            new AuditLog(5L, "24/10/2026 11:20", "Chị Linh", "Recruiter", "Chuyển vòng Pipeline", "Duyệt sơ loại hồ sơ Nguyễn Văn An, chuyển sang PV Kỹ thuật")
        );
    }

    // ==========================================
    // CONTROLLER ENDPOINTS (HR PORTAL)
    // ==========================================

    @GetMapping({"", "/", "/recruitment", "/recruitment/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("jobs", getMockJobs());
        model.addAttribute("candidates", getMockCandidates());
        model.addAttribute("interviews", getMockInterviews());
        model.addAttribute("activeMenu", "dashboard");
        return "recruitment/dashboard";
    }

    @GetMapping("/recruitment/requisitions")
    public String requisitions(Model model) {
        model.addAttribute("requisitions", getMockRequisitions());
        model.addAttribute("activeMenu", "requisitions");
        return "recruitment/requisitions";
    }

    @GetMapping("/recruitment/jobs")
    public String jobs(Model model) {
        model.addAttribute("jobs", getMockJobs());
        model.addAttribute("activeMenu", "jobs");
        return "recruitment/jobs";
    }

    @GetMapping("/recruitment/pipeline")
    public String pipeline(@RequestParam(value = "jobId", required = false) Long jobId, Model model) {
        List<Candidate> allCandidates = getMockCandidates();
        List<Candidate> filtered = (jobId != null && jobId > 0)
                ? allCandidates.stream().filter(c -> c.jobId().equals(jobId)).toList()
                : allCandidates;

        model.addAttribute("jobs", getMockJobs());
        model.addAttribute("selectedJobId", jobId != null ? jobId : 0L);
        model.addAttribute("candidates", filtered);

        model.addAttribute("newCandidates", filtered.stream().filter(c -> "NEW".equals(c.stage())).toList());
        model.addAttribute("screeningCandidates", filtered.stream().filter(c -> "SCREENING".equals(c.stage())).toList());
        model.addAttribute("assessmentCandidates", filtered.stream().filter(c -> "ASSESSMENT".equals(c.stage())).toList());
        model.addAttribute("techCandidates", filtered.stream().filter(c -> "TECH_INTERVIEW".equals(c.stage())).toList());
        model.addAttribute("hrCandidates", filtered.stream().filter(c -> "HR_INTERVIEW".equals(c.stage())).toList());
        model.addAttribute("offerCandidates", filtered.stream().filter(c -> "OFFER".equals(c.stage())).toList());
        model.addAttribute("hiredCandidates", filtered.stream().filter(c -> "HIRED".equals(c.stage())).toList());
        model.addAttribute("rejectedCandidates", filtered.stream().filter(c -> "REJECTED".equals(c.stage())).toList());

        model.addAttribute("activeMenu", "pipeline");
        return "recruitment/pipeline";
    }

    @GetMapping("/recruitment/candidates")
    public String candidates(Model model) {
        model.addAttribute("candidates", getMockCandidates());
        model.addAttribute("jobs", getMockJobs());
        model.addAttribute("activeMenu", "candidates");
        return "recruitment/candidates";
    }

    @GetMapping("/recruitment/candidate/{id}")
    public String candidateDetail(@PathVariable("id") Long id, Model model) {
        Candidate candidate = getMockCandidates().stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElse(getMockCandidates().get(0));

        model.addAttribute("candidate", candidate);
        model.addAttribute("activeMenu", "candidates");
        return "recruitment/candidate-detail";
    }

    @GetMapping("/recruitment/interviews")
    public String interviews(Model model) {
        model.addAttribute("interviews", getMockInterviews());
        model.addAttribute("activeMenu", "interviews");
        return "recruitment/interviews";
    }

    // Giao bài test & đánh giá năng lực
    @GetMapping("/recruitment/assessments")
    public String assessments(Model model) {
        model.addAttribute("assessments", getMockAssessments());
        model.addAttribute("candidates", getMockCandidates());
        model.addAttribute("activeMenu", "assessments");
        return "recruitment/assessments";
    }

    // Offer & Phê duyệt offer
    @GetMapping("/recruitment/offers")
    public String offers(Model model) {
        model.addAttribute("offers", getMockOffers());
        model.addAttribute("candidates", getMockCandidates());
        model.addAttribute("activeMenu", "offers");
        return "recruitment/offers";
    }

    // Onboarding cơ bản (checklist)
    @GetMapping("/recruitment/onboarding")
    public String onboarding(Model model) {
        model.addAttribute("onboardingList", getMockOnboarding());
        model.addAttribute("activeMenu", "onboarding");
        return "recruitment/onboarding";
    }

    // Dashboard, báo cáo
    @GetMapping("/recruitment/reports")
    public String reports(Model model) {
        model.addAttribute("activeMenu", "reports");
        return "recruitment/reports";
    }

    // Export Excel / CSV Download
    @GetMapping("/recruitment/export-excel")
    public ResponseEntity<byte[]> exportExcel() {
        StringBuilder csv = new StringBuilder();
        csv.append("\uFEFF"); // BOM for Excel UTF-8
        csv.append("Mã hồ sơ,Họ và tên,Email,Số điện thoại,Vị trí ứng tuyển,Kinh nghiệm,Trạng thái vòng,Điểm đánh giá,Nguồn ứng viên,Ngày nộp\n");
        for (Candidate c : getMockCandidates()) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%.1f\",\"%s\",\"%s\"\n",
                    c.applicationCode(), c.fullName(), c.email(), c.phone(), c.appliedJobTitle(),
                    c.experience(), c.stage(), c.rating(), c.source(), c.applyDate()));
        }
        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Bao_Cao_Tuyen_Dung_2026.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    // Quản trị người dùng, vai trò, phòng ban, cấu hình quy trình, email template, audit log
    @GetMapping("/recruitment/settings")
    public String settings(Model model) {
        model.addAttribute("auditLogs", getMockAuditLogs());
        model.addAttribute("activeMenu", "settings");
        return "recruitment/settings";
    }

    @GetMapping("/recruitment/requirements")
    public String requirementsDoc(Model model) {
        model.addAttribute("activeMenu", "requirements");
        return "recruitment/requirements";
    }

    // Inbound Webhook mô phỏng nhận bài test tự động qua Email reply
    @ResponseBody
    @PostMapping("/recruitment/api/webhook/email-reply")
    public Map<String, Object> emailReplyWebhook(@RequestBody(required = false) Map<String, Object> payload) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("message", "Inbound Webhook đã phân tích email reply thành công và tự động đính kèm bài test vào hồ sơ ứng viên.");
        res.put("receivedAt", new Date().toString());
        return res;
    }

    // REST API endpoints for Flutter & AJAX
    @ResponseBody
    @GetMapping("/recruitment/api/jobs")
    public List<JobPosting> apiGetJobs() {
        return getMockJobs();
    }

    @ResponseBody
    @GetMapping("/recruitment/api/candidates")
    public List<Candidate> apiGetCandidates(@RequestParam(required = false) Long jobId) {
        if (jobId != null) {
            return getMockCandidates().stream().filter(c -> c.jobId().equals(jobId)).toList();
        }
        return getMockCandidates();
    }

    @ResponseBody
    @GetMapping("/recruitment/api/interviews")
    public List<InterviewSchedule> apiGetInterviews() {
        return getMockInterviews();
    }
}
