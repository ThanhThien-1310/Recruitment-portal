package com.example.quanlytuyendung.services;

import com.example.quanlytuyendung.entities.*;
import com.example.quanlytuyendung.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateRepository candidateRepository;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final OnboardingRepository onboardingRepository;
    private final AuditLogRepository auditLogRepository;

    public DataInitializer(UserRepository userRepository,
                           JobPostingRepository jobPostingRepository,
                           CandidateRepository candidateRepository,
                           InterviewRepository interviewRepository,
                           OfferRepository offerRepository,
                           OnboardingRepository onboardingRepository,
                           AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.candidateRepository = candidateRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.onboardingRepository = onboardingRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Users (HR_MANAGER, RECRUITER, INTERVIEWER)
        if (userRepository.count() == 0) {
            log.info("Khởi tạo tài khoản người dùng mặc định...");
            userRepository.save(new UserEntity("admin", "admin123", "Nguyễn Văn Hùng (HR Lead)", "hung.hr@techcorp.vn", "Phòng Nhân sự", "HR_MANAGER"));
            userRepository.save(new UserEntity("recruiter", "recruiter123", "Trần Thu Hà (Recruiter)", "ha.tran@techcorp.vn", "Phòng Tuyển dụng", "RECRUITER"));
            userRepository.save(new UserEntity("interviewer", "interviewer123", "Lê Thanh Thiện (Tech Lead)", "thien.le@techcorp.vn", "Phòng Kỹ thuật & Công nghệ", "INTERVIEWER"));
        }

        // 2. Seed Job Postings
        if (jobPostingRepository.count() == 0) {
            log.info("Khởi tạo danh sách tin tuyển dụng mặc định...");
            jobPostingRepository.save(new JobPostingEntity(
                    "Senior Java Backend Engineer", "Phòng Kỹ thuật & Công nghệ", "Hà Nội / Hybrid", "Toàn thời gian", "25 - 35 Triệu VNĐ",
                    2, "15/11/2026", "Đang tuyển", 28,
                    "Phát triển hệ thống microservices backend hiệu năng cao, tối ưu hóa cơ sở dữ liệu lớn và thiết kế kiến trúc hệ thống.",
                    "Tối thiểu 3 năm kinh nghiệm với Java, Spring Boot, MySQL/PostgreSQL, Redis, Kafka, Docker.",
                    "Thưởng tháng 13 + KPIs, bảo hiểm sức khỏe quốc tế, cấp Macbook Pro M3.", 3
            ));

            jobPostingRepository.save(new JobPostingEntity(
                    "Flutter Mobile Developer", "Phòng Kỹ thuật & Công nghệ", "Hà Nội / Remote", "Toàn thời gian", "18 - 28 Triệu VNĐ",
                    3, "20/11/2026", "Đang tuyển", 35,
                    "Xây dựng ứng dụng mobile đa nền tảng (iOS & Android) phục vụ hàng trăm nghìn người dùng, tích hợp REST API.",
                    "Tối thiểu 2 năm kinh nghiệm Flutter/Dart, hiểu sâu về State Management (Bloc/Provider).",
                    "Môi trường Agile năng động, phụ cấp cơm trưa + gửi xe, đào tạo nâng cao chuyên môn.", 2
            ));

            jobPostingRepository.save(new JobPostingEntity(
                    "UI/UX Product Designer", "Phòng Sản phẩm & Thiết kế", "TP. Hồ Chí Minh", "Toàn thời gian", "16 - 24 Triệu VNĐ",
                    1, "10/11/2026", "Đang tuyển", 19,
                    "Thiết kế Design System, User Journey và Wireframe/Prototype trực quan cho Web và Mobile.",
                    "Sử dụng thành thạo Figma, tư duy User-Centric, có portfolio dự án ấn tượng.",
                    "Màn hình 4K, khám sức khỏe định kỳ, thưởng các dịp lễ tết.", 1
            ));

            jobPostingRepository.save(new JobPostingEntity(
                    "Chuyên viên Tuyển dụng & Đào tạo (HR Recruiter)", "Phòng Nhân sự", "Hà Nội", "Toàn thời gian", "12 - 18 Triệu VNĐ",
                    1, "05/11/2026", "Đang tuyển", 14,
                    "Chịu trách nhiệm toàn bộ quy trình sourcing, săn đón nhân tài công nghệ và phỏng vấn sơ loại.",
                    "Tối thiểu 1 năm kinh nghiệm IT Recruitment, mạng lưới ứng viên công nghệ tốt.",
                    "Hoa hồng tuyển dụng hấp dẫn, làm việc từ thứ 2 đến thứ 6, du lịch hàng năm.", 1
            ));

            jobPostingRepository.save(new JobPostingEntity(
                    "Thực tập sinh Lập trình Web (Spring Boot & Vue/React)", "Phòng Kỹ thuật & Công nghệ", "Hà Nội", "Thực tập", "4 - 6 Triệu VNĐ",
                    5, "30/11/2026", "Đang tuyển", 52,
                    "Tham gia trực tiếp vào các module thực tế của dự án, được Mentor 1-1 chỉ dẫn chi tiết.",
                    "Sinh viên năm 3, 4 hoặc mới tốt nghiệp CNTT, nắm vững căn bản OOP, Java Core, SQL.",
                    "Hỗ trợ dấu thực tập và đồ án tốt nghiệp, phụ cấp ăn trưa.", 0
            ));
        }

        // 3. Seed Candidates across stages
        if (candidateRepository.count() == 0) {
            log.info("Khởi tạo danh sách ứng viên mặc định...");
            candidateRepository.save(new CandidateEntity("Nguyễn Văn An", "an.nguyen@gmail.com", "0901234567", "Senior Java Backend Engineer", 1L, "SCREENING", 4.5, "https://drive.google.com/file/d/sample_cv_an/view", "Java, Spring Boot, MySQL, Kafka", 4, "PASSED"));
            candidateRepository.save(new CandidateEntity("Trần Thị Mai", "mai.tran@gmail.com", "0912345678", "Flutter Mobile Developer", 2L, "TECH_INTERVIEW", 4.7, "https://drive.google.com/file/d/sample_cv_mai/view", "Flutter, Dart, Provider, REST API", 3, "PASSED"));
            candidateRepository.save(new CandidateEntity("Lê Hoàng Long", "long.le@gmail.com", "0923456789", "UI/UX Product Designer", 3L, "HR_ROUND", 4.2, "https://drive.google.com/file/d/sample_cv_long/view", "Figma, User Journey, Wireframing", 2, "PASSED"));
            candidateRepository.save(new CandidateEntity("Phạm Quốc Bảo", "bao.pham@gmail.com", "0934567890", "Senior Java Backend Engineer", 1L, "OFFER", 4.8, "https://drive.google.com/file/d/sample_cv_bao/view", "Microservices, Spring Cloud, Docker", 5, "PASSED"));
            candidateRepository.save(new CandidateEntity("Đỗ Minh Khoa", "khoa.do@gmail.com", "0945678901", "Flutter Mobile Developer", 2L, "HIRED", 4.9, "https://drive.google.com/file/d/sample_cv_khoa/view", "Flutter, Bloc, iOS Native", 3, "PASSED"));
            candidateRepository.save(new CandidateEntity("Hoàng Văn Đức", "duc.hoang@gmail.com", "0956789012", "Flutter Mobile Developer", 2L, "NEW", 3.8, "https://drive.google.com/file/d/sample_cv_duc/view", "Flutter Basic, Dart", 1, "PASSED"));
        }

        // 4. Seed Interviews
        if (interviewRepository.count() == 0) {
            log.info("Khởi tạo lịch phỏng vấn mẫu...");
            InterviewEntity iv1 = new InterviewEntity(1L, "Nguyễn Văn An", "Senior Java Backend Engineer", "Hôm nay", "09:30 - 10:30", "Phỏng vấn Kỹ thuật (Vòng 1)", "Anh Thiện (Lead) & Chị Hà (HR)", "Trực tuyến (Google Meet)", "https://meet.google.com/abc-defg-hij", "Sắp diễn ra");
            InterviewEntity iv2 = new InterviewEntity(2L, "Trần Thị Mai", "Flutter Mobile Developer", "Hôm nay", "14:00 - 15:00", "Phỏng vấn Kỹ thuật & Live Coding", "Chị Linh (HR) & Anh Hưng (Mobile Lead)", "Trực tiếp tại Phòng họp 302", "", "Sắp diễn ra");
            InterviewEntity iv3 = new InterviewEntity(3L, "Lê Hoàng Long", "UI/UX Product Designer", "Ngày mai", "10:00 - 11:00", "Phỏng vấn Đánh giá Văn hóa & Deal lương", "Anh Tuấn (Design Lead)", "Trực tuyến (Google Meet)", "https://meet.google.com/xyz-uvwx-rst", "Đã lên lịch");

            interviewRepository.save(iv1);
            interviewRepository.save(iv2);
            interviewRepository.save(iv3);
        }

        // 5. Seed Offers
        if (offerRepository.count() == 0) {
            log.info("Khởi tạo các đề xuất Offer mẫu...");
            OfferEntity off1 = new OfferEntity(4L, "Phạm Quốc Bảo", "bao.pham@gmail.com", "Senior Java Backend Engineer", "Phòng Kỹ thuật & Công nghệ", "32.000.000 VNĐ", "10.000.000 VNĐ", "01/12/2026", "PENDING", "Thu Hà (HR Manager)");
            OfferEntity off2 = new OfferEntity(5L, "Đỗ Minh Khoa", "khoa.do@gmail.com", "Flutter Mobile Developer", "Phòng Kỹ thuật & Công nghệ", "24.000.000 VNĐ", "5.000.000 VNĐ", "15/11/2026", "APPROVED", "Nguyễn Văn Hùng (HR Lead)");
            offerRepository.save(off1);
            offerRepository.save(off2);
        }

        // 6. Seed Onboarding Tasks
        if (onboardingRepository.count() == 0) {
            log.info("Khởi tạo danh sách Onboarding mẫu...");
            onboardingRepository.save(new OnboardingEntity(5L, "Đỗ Minh Khoa", "Tạo tài khoản email công ty & Slack/Jira", "IT Support", "Trần Bình", "14/11/2026", "HOÀN_THÀNH"));
            onboardingRepository.save(new OnboardingEntity(5L, "Đỗ Minh Khoa", "Bàn giao máy tính MacBook Pro M3 & thẻ ra vào", "Hành chính", "Nguyễn Thoa", "15/11/2026", "ĐANG_LÀM"));
            onboardingRepository.save(new OnboardingEntity(5L, "Đỗ Minh Khoa", "Ký kết hợp đồng lao động chính thức & khai thuế", "Nhân sự", "Thu Hà", "15/11/2026", "CHƯA_LÀM"));
        }

        // 7. Seed Audit Logs
        if (auditLogRepository.count() == 0) {
            auditLogRepository.save(new AuditLogEntity("admin", "HR_MANAGER", "SYSTEM_INIT", "Database", "127.0.0.1", "Hệ thống ATS khởi động thành công với cơ sở dữ liệu MySQL"));
            auditLogRepository.save(new AuditLogEntity("recruiter", "RECRUITER", "OFFER_CREATE", "Candidate #4 (Phạm Quốc Bảo)", "192.168.1.15", "Tạo đề xuất thư mời nhận việc mức lương 32M"));
        }

        log.info("Dữ liệu khởi tạo thành công!");
    }
}
