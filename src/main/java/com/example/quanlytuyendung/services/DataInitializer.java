package com.example.quanlytuyendung.services;

import com.example.quanlytuyendung.entities.*;
import com.example.quanlytuyendung.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           JobPostingRepository jobPostingRepository,
                           CandidateRepository candidateRepository,
                           InterviewRepository interviewRepository,
                           OfferRepository offerRepository,
                           OnboardingRepository onboardingRepository,
                           AuditLogRepository auditLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.candidateRepository = candidateRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.onboardingRepository = onboardingRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Users & Tự động mã hóa mật khẩu bằng BCrypt
        if (userRepository.count() == 0) {
            log.info("Khởi tạo tài khoản người dùng mặc định (Mã hóa mật khẩu với BCrypt)...");
            userRepository.save(new UserEntity("admin", passwordEncoder.encode("admin123"), "Nguyễn Văn Hùng (HR Lead)", "hung.hr@techcorp.vn", "Phòng Nhân sự", "HR_MANAGER"));
            userRepository.save(new UserEntity("recruiter", passwordEncoder.encode("recruiter123"), "Trần Thu Hà (Recruiter)", "ha.tran@techcorp.vn", "Phòng Tuyển dụng", "RECRUITER"));
            userRepository.save(new UserEntity("interviewer", passwordEncoder.encode("interviewer123"), "Lê Thanh Thiện (Tech Lead)", "thien.le@techcorp.vn", "Phòng Kỹ thuật & Công nghệ", "INTERVIEWER"));
        } else {
            // Nâng cấp toàn bộ mật khẩu dạng plain text cũ trong MySQL sang mã hóa BCrypt
            for (UserEntity user : userRepository.findAll()) {
                String pwd = user.getPassword();
                if (pwd != null && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$") && !pwd.startsWith("$2y$")) {
                    user.setPassword(passwordEncoder.encode(pwd));
                    userRepository.save(user);
                    log.info("Đã nâng cấp mật khẩu tài khoản '{}' sang mã hóa BCrypt an toàn.", user.getUsername());
                }
            }
        }

        // 2. Seed 18 Job Postings chất lượng cao, rõ ràng, chi tiết từng phòng ban
        if (jobPostingRepository.count() < 18) {
            log.info("Khởi tạo hệ thống 18 tin tuyển dụng chất lượng cao đa phòng ban...");
            jobPostingRepository.deleteAll();

            // 1. Senior Java Backend Engineer
            jobPostingRepository.save(new JobPostingEntity(
                    "Senior Java Backend Engineer", "Phòng Kỹ thuật & Công nghệ", "Hà Nội / Hybrid", "Toàn thời gian", "28 - 42 Triệu VNĐ",
                    2, "30/11/2026", "Đang tuyển", 28,
                    "Thiết kế, xây dựng và tối ưu hóa hệ thống microservices backend chịu tải cao (High Concurrency) phục vụ hàng triệu người dùng. Tối ưu kiến trúc phân tán, message queue Kafka và cơ sở dữ liệu lớn. Phối hợp với đội ngũ DevOps và Frontend để triển khai sản phẩm.",
                    "Tối thiểu 3 năm kinh nghiệm với Java Core (Java 17/21), Spring Boot 3, Spring Cloud. Thành thạo PostgreSQL/MySQL, Redis caching, Kafka message broker. Nắm vững Clean Architecture, Design Patterns, Docker & Kubernetes cơ bản.",
                    "Lương cạnh tranh 28 - 42M, thưởng tháng 13 + KPIs quý, bảo hiểm PVI Care quốc tế, cấp MacBook Pro M3 Max, hỗ trợ 2 ngày làm việc từ xa (WFH) mỗi tuần.", 3
            ));

            // 2. Fullstack Developer (Java & ReactJS)
            jobPostingRepository.save(new JobPostingEntity(
                    "Fullstack Developer (Java & ReactJS)", "Phòng Kỹ thuật & Công nghệ", "TP. Hồ Chí Minh", "Toàn thời gian", "22 - 35 Triệu VNĐ",
                    3, "15/12/2026", "Đang tuyển", 18,
                    "Phát triển toàn diện từ giao diện người dùng hiện đại bằng ReactJS/Next.js đến hệ thống backend xử lý nghiệp vụ với Spring Boot. Tối ưu hóa trải nghiệm người dùng, tốc độ tải trang và xây dựng RESTful API chuẩn mực.",
                    "Tối thiểu 2 năm kinh nghiệm làm việc với Java (Spring Boot) và ReactJS/TypeScript. Thành thạo TailwindCSS, Redux Toolkit, REST API, MySQL/PostgreSQL. Có tư duy thẩm mỹ UI/UX tốt.",
                    "Lương 22 - 35M, môi trường Agile năng động, phụ cấp ăn trưa và gửi xe, bảo hiểm sức khỏe toàn diện, review lương 2 lần/năm.", 2
            ));

            // 3. DevOps & Cloud Infrastructure Engineer (AWS/K8s)
            jobPostingRepository.save(new JobPostingEntity(
                    "DevOps & Cloud Infrastructure Engineer (AWS/K8s)", "Phòng Kỹ thuật & Công nghệ", "Hà Nội / Remote", "Toàn thời gian", "25 - 40 Triệu VNĐ",
                    2, "10/12/2026", "Đang tuyển", 12,
                    "Thiết kế và duy trì hạ tầng Cloud trên AWS/GCP, thiết lập đường ống CI/CD tự động hóa (GitLab CI / GitHub Actions). Giám sát hệ thống (Prometheus, Grafana, ELK), bảo mật mạng và tối ưu chi phí hạ tầng điện toán đám mây.",
                    "Tối thiểu 3 năm kinh nghiệm DevOps. Thành thạo AWS (EC2, EKS, RDS, S3), Docker, Kubernetes, Terraform/Ansible. Hiểu sâu về Linux OS, Networking, Nginx, CI/CD pipeline.",
                    "Lương 25 - 40M, làm việc Remote linh hoạt, tài trợ 100% kinh phí thi chứng chỉ AWS/CKA quốc tế, thưởng dự án theo quý.", 3
            ));

            // 4. AI & Machine Learning Engineer (Python/LLM)
            jobPostingRepository.save(new JobPostingEntity(
                    "AI & Machine Learning Engineer (Python/LLM)", "Phòng Kỹ thuật & Công nghệ", "TP. Hồ Chí Minh / Hybrid", "Toàn thời gian", "30 - 50 Triệu VNĐ",
                    2, "20/12/2026", "Đang tuyển", 22,
                    "Nghiên cứu, phát triển và tích hợp các mô hình Generative AI, Large Language Models (LLM), RAG (Retrieval-Augmented Generation) và NLP để tự động hóa trích xuất CV và đánh giá ứng viên thông minh trong hệ thống ATS.",
                    "Tối thiểu 2 năm kinh nghiệm với Python, PyTorch/TensorFlow, LangChain, LlamaIndex, OpenAI/Gemini API, Vector Database (Milvus/Chroma). Nắm vững thuật toán xử lý ngôn ngữ tự nhiên (NLP).",
                    "Mức lương dẫn đầu thị trường 30 - 50M, thưởng cổ phần (ESOP), ngân sách tham dự hội nghị công nghệ quốc tế, trang bị máy trạm GPU cao cấp.", 2
            ));

            // 5. Flutter Mobile Developer
            jobPostingRepository.save(new JobPostingEntity(
                    "Flutter Mobile Developer", "Phòng Kỹ thuật & Công nghệ", "TP. Hồ Chí Minh / Remote", "Toàn thời gian", "20 - 32 Triệu VNĐ",
                    3, "15/11/2026", "Đang tuyển", 35,
                    "Xây dựng và tối ưu ứng dụng di động đa nền tảng (iOS & Android) phục vụ hàng trăm nghìn người dùng. Tích hợp REST API, WebSockets, Firebase Push Notification và tối ưu hiệu năng render 60fps mượt mà.",
                    "Tối thiểu 2 năm kinh nghiệm thực tế với Flutter & Dart. Nắm vững kiến trúc Bloc / Provider / Riverpod. Có sản phẩm đã phát hành trên App Store / Google Play.",
                    "Lương 20 - 32M, cơ hội làm việc với các chuyên gia mobile hàng đầu, teambuilding 2 lần/năm, hỗ trợ thiết bị test iPhone/Android mới nhất.", 2
            ));

            // 6. Senior iOS Native Developer (Swift/SwiftUI)
            jobPostingRepository.save(new JobPostingEntity(
                    "Senior iOS Native Developer (Swift/SwiftUI)", "Phòng Kỹ thuật & Công nghệ", "Hà Nội", "Toàn thời gian", "25 - 38 Triệu VNĐ",
                    1, "05/12/2026", "Đang tuyển", 15,
                    "Chịu trách nhiệm kiến trúc và phát triển ứng dụng iOS native cao cấp cho hệ thống tài chính & nhân sự. Tối ưu bộ nhớ, Core Data, Apple Pay và trải nghiệm người dùng theo Human Interface Guidelines.",
                    "Tối thiểu 3 năm kinh nghiệm lập trình iOS với Swift và SwiftUI/UIKit. Nắm vững Combine/Async-Await, Memory Management, Unit Testing trên iOS.",
                    "Lương 25 - 38M, thưởng vượt chỉ tiêu, bảo hiểm gia đình, tài trợ thiết bị Apple sinh thái đầy đủ (MacBook + iPhone Pro).", 3
            ));

            // 7. Frontend React / Next.js Engineer
            jobPostingRepository.save(new JobPostingEntity(
                    "Frontend React / Next.js Engineer", "Phòng Kỹ thuật & Công nghệ", "TP. Hồ Chí Minh", "Toàn thời gian", "18 - 28 Triệu VNĐ",
                    2, "12/12/2026", "Đang tuyển", 31,
                    "Xây dựng các giao diện Single Page Application (SPA) và Server-Side Rendering (SSR) hiện đại với ReactJS 18+ và Next.js. Tối ưu Core Web Vitals, SEO và độ phản hồi tức thì cho người dùng.",
                    "Tối thiểu 2 năm kinh nghiệm Frontend. Thành thạo TypeScript, ReactJS, Next.js, TailwindCSS, HTML5/CSS3. Hiểu sâu về Web Performance và responsive design.",
                    "Lương 18 - 28M, thưởng quý, phụ cấp ăn trưa, môi trường trẻ trung cởi mở, chế độ đào tạo tiếng Anh và kỹ năng mềm.", 2
            ));

            // 8. QA / Automation Test Engineer (Selenium/Playwright)
            jobPostingRepository.save(new JobPostingEntity(
                    "QA / Automation Test Engineer (Selenium/Playwright)", "Phòng Kỹ thuật & Công nghệ", "Hà Nội / Đà Nẵng", "Toàn thời gian", "16 - 25 Triệu VNĐ",
                    2, "25/11/2026", "Đang tuyển", 19,
                    "Thiết kế kịch bản kiểm thử tự động (Automation Test) cho Web và Mobile. Tích hợp kiểm thử vào CI/CD pipeline, kiểm thử hiệu năng (JMeter) và kiểm thử bảo mật API.",
                    "Tối thiểu 2 năm kinh nghiệm QA/QC. Có kinh nghiệm viết Automation Test với Java/Python sử dụng Selenium WebDriver hoặc Playwright/Cypress. Hiểu rõ quy trình Agile/Scrum.",
                    "Lương 16 - 25M, lộ trình thăng tiến rõ ràng lên QA Lead, khám sức khỏe cao cấp định kỳ, thưởng tháng 13.", 2
            ));

            // 9. Data Engineer (Spark/Airflow/Kafka)
            jobPostingRepository.save(new JobPostingEntity(
                    "Data Engineer (Spark/Airflow/Kafka)", "Phòng Kỹ thuật & Công nghệ", "TP. Hồ Chí Minh", "Toàn thời gian", "25 - 42 Triệu VNĐ",
                    1, "18/12/2026", "Đang tuyển", 14,
                    "Xây dựng và vận hành hệ thống Data Pipeline, Data Lake và Data Warehouse phục vụ báo cáo BI và các mô hình Machine Learning. Xử lý dữ liệu lớn theo thời gian thực (Real-time Streaming).",
                    "Tối thiểu 2 năm kinh nghiệm Data Engineering. Thành thạo SQL nâng cao, Python/Scala, Apache Spark, Apache Airflow, Kafka, Snowflake hoặc BigQuery.",
                    "Lương 25 - 42M, phụ cấp công tác, thưởng hiệu suất hàng năm, cơ hội làm việc với tập dữ liệu quy mô hàng chục Terabytes.", 2
            ));

            // 10. UI/UX Product Designer
            jobPostingRepository.save(new JobPostingEntity(
                    "UI/UX Product Designer", "Phòng Sản phẩm & Thiết kế", "TP. Hồ Chí Minh", "Toàn thời gian", "18 - 28 Triệu VNĐ",
                    2, "10/12/2026", "Đang tuyển", 26,
                    "Xây dựng Design System đồng nhất, thực hiện nghiên cứu người dùng (User Research), thiết kế User Journey, Wireframe và Interactive Prototype sắc nét cho hệ thống ATS và cổng thông tin việc làm.",
                    "Tối thiểu 2 năm kinh nghiệm thiết kế UI/UX cho sản phẩm Web/App. Thành thạo Figma, Design Tokens, Micro-interactions. Có Portfolio dự án thực tế thuyết phục.",
                    "Lương 18 - 28M, trang bị màn hình Dell UltraSharp 4K, tham gia các khóa học chuyên sâu Design Leadership, teambuilding định kỳ.", 2
            ));

            // 11. Senior Product Owner (FinTech & HR SaaS)
            jobPostingRepository.save(new JobPostingEntity(
                    "Senior Product Owner (FinTech & HR SaaS)", "Phòng Sản phẩm & Thiết kế", "Hà Nội", "Toàn thời gian", "30 - 45 Triệu VNĐ",
                    1, "20/12/2026", "Đang tuyển", 17,
                    "Định hình tầm nhìn sản phẩm (Product Vision), quản lý và ưu tiên Product Backlog, phân tích chỉ số kinh doanh (Metrics) và phối hợp cùng đội ngũ kỹ thuật phát triển các tính năng đột phá.",
                    "Tối thiểu 3 năm kinh nghiệm ở vai trò Product Owner hoặc Product Manager cho các sản phẩm B2B SaaS hoặc FinTech. Kỹ năng giao tiếp và tư duy phân tích dữ liệu xuất sắc.",
                    "Lương 30 - 45M, thưởng theo mức độ tăng trưởng của sản phẩm, gói chăm sóc sức khỏe gia đình, quyền chọn cổ phiếu ưu đãi.", 3
            ));

            // 12. Business Analyst (BA IT / Enterprise)
            jobPostingRepository.save(new JobPostingEntity(
                    "Business Analyst (BA IT / Enterprise)", "Phòng Sản phẩm & Thiết kế", "Hà Nội / Đà Nẵng", "Toàn thời gian", "18 - 30 Triệu VNĐ",
                    2, "15/12/2026", "Đang tuyển", 23,
                    "Khảo sát, thu thập và phân tích yêu cầu nghiệp vụ từ các bên liên quan. Viết tài liệu đặc tả chức năng (BRD, SRS, Use Case), phối hợp chặt chẽ với Developers và QA trong suốt quá trình phát triển.",
                    "Tối thiểu 2 năm kinh nghiệm BA mảng phần mềm. Sử dụng thành thạo Jira, Confluence, Draw.io/Visio, biết viết truy vấn SQL cơ bản. Tiếng Anh giao tiếp tốt là lợi thế lớn.",
                    "Lương 18 - 30M, lộ trình thăng tiến rõ ràng lên Senior/Lead BA, review lương 2 lần/năm, thưởng dự án hoàn thành đúng tiến độ.", 2
            ));

            // 13. Brand & Motion Graphic Designer
            jobPostingRepository.save(new JobPostingEntity(
                    "Brand & Motion Graphic Designer", "Phòng Sản phẩm & Thiết kế", "TP. Hồ Chí Minh", "Toàn thời gian", "15 - 22 Triệu VNĐ",
                    1, "30/11/2026", "Đang tuyển", 19,
                    "Sáng tạo các ấn phẩm truyền thông thương hiệu, video animation giới thiệu tính năng sản phẩm, đồ họa mạng xã hội và banner tuyển dụng chuyên nghiệp.",
                    "Tối thiểu 1 năm kinh nghiệm thiết kế đồ họa & motion. Thành thạo Adobe After Effects, Premiere, Illustrator, Photoshop. Có gu thẩm mỹ hiện đại, sáng tạo.",
                    "Lương 15 - 22M, môi trường sáng tạo tự do, trang bị máy tính đồ họa cấu hình cao, phụ cấp trà chiều & ăn nhẹ hàng ngày.", 1
            ));

            // 14. Chuyên viên Tuyển dụng & Đào tạo (Senior IT Recruiter)
            jobPostingRepository.save(new JobPostingEntity(
                    "Chuyên viên Tuyển dụng & Đào tạo (Senior IT Recruiter)", "Phòng Nhân sự", "Hà Nội", "Toàn thời gian", "15 - 24 Triệu VNĐ",
                    2, "05/12/2026", "Đang tuyển", 25,
                    "Chịu trách nhiệm toàn bộ phễu tuyển dụng nhân sự công nghệ từ tìm kiếm ứng viên (Headhunting), phỏng vấn sơ loại, điều phối lịch hẹn đến thương lượng Offer và chăm sóc Onboarding.",
                    "Tối thiểu 2 năm kinh nghiệm tuyển dụng IT. Mạng lưới ứng viên công nghệ rộng lớn trên LinkedIn/TopCV. Kỹ năng giao tiếp, thương lượng và thuyết phục xuất sắc.",
                    "Lương cứng 15 - 24M + Hoa hồng tuyển dụng hấp dẫn không giới hạn, du lịch nghỉ dưỡng hàng năm, bảo hiểm sức khỏe quốc tế.", 2
            ));

            // 15. HR Generalist & Chuyên viên C&B (Tiền lương & Phúc lợi)
            jobPostingRepository.save(new JobPostingEntity(
                    "HR Generalist & Chuyên viên C&B (Tiền lương & Phúc lợi)", "Phòng Nhân sự", "TP. Hồ Chí Minh", "Toàn thời gian", "14 - 20 Triệu VNĐ",
                    1, "12/12/2026", "Đang tuyển", 16,
                    "Quản lý hợp đồng lao động, tính lương, thưởng, bảo hiểm xã hội, thuế thu nhập cá nhân và thực thi các chính sách phúc lợi nhân viên. Tổ chức các hoạt động gắn kết nội bộ công ty.",
                    "Tối thiểu 2 năm kinh nghiệm mảng C&B và HR Generalist. Nắm vững Luật Lao động, BHXH, thuế TNCN. Cẩn thận, bảo mật thông tin và thành thạo Excel/phần mềm nhân sự.",
                    "Lương 14 - 20M, thưởng lễ tết chu đáo, làm việc giờ hành chính từ thứ 2 đến thứ 6, môi trường làm việc thân thiện, hòa đồng.", 2
            ));

            // 16. Digital Marketing Specialist (Growth & Performance)
            jobPostingRepository.save(new JobPostingEntity(
                    "Digital Marketing Specialist (Growth & Performance)", "Phòng Marketing & Kinh doanh", "TP. Hồ Chí Minh", "Toàn thời gian", "16 - 26 Triệu VNĐ",
                    2, "15/12/2026", "Đang tuyển", 27,
                    "Lên kế hoạch và tối ưu hóa các chiến dịch quảng cáo trực tuyến (Google Ads, Facebook Ads, LinkedIn Ads, SEO) nhằm thu hút khách hàng doanh nghiệp và xây dựng thương hiệu nhà tuyển dụng hàng đầu.",
                    "Tối thiểu 2 năm kinh nghiệm Digital Marketing. Thành thạo Google Analytics 4, Meta Ads Manager, các công cụ SEO (Ahrefs, Semrush). Tư duy định hướng số liệu (Data-driven).",
                    "Lương 16 - 26M + Thưởng KPI chiến dịch, ngân sách quảng cáo lớn để thử nghiệm ý tưởng mới, hỗ trợ tham gia các hội thảo Marketing chuyên nghiệp.", 2
            ));

            // 17. B2B Tech Enterprise Sales Specialist
            jobPostingRepository.save(new JobPostingEntity(
                    "B2B Tech Enterprise Sales Specialist", "Phòng Marketing & Kinh doanh", "Hà Nội", "Toàn thời gian", "18 - 35 Triệu VNĐ",
                    3, "20/12/2026", "Đang tuyển", 20,
                    "Tìm kiếm, tiếp cận và tư vấn giải pháp phần mềm quản trị tuyển dụng ATS cho các doanh nghiệp vừa và lớn. Đàm phán ký kết hợp đồng, chăm sóc mối quan hệ khách hàng chiến lược.",
                    "Tối thiểu 2 năm kinh nghiệm Sales B2B trong lĩnh vực phần mềm SaaS, CNTT hoặc nhân sự. Kỹ năng thuyết trình, đàm phán thương mại và chốt deal xuất sắc.",
                    "Lương cơ bản 18 - 35M + % Hoa hồng hợp đồng cực cao (Thu nhập thực tế 40 - 70M/tháng), phụ cấp tiếp khách và đi lại.", 2
            ));

            // 18. Thực tập sinh Lập trình Web & AI (Intern Web/AI)
            jobPostingRepository.save(new JobPostingEntity(
                    "Thực tập sinh Lập trình Web & AI (Intern Web/AI)", "Phòng Kỹ thuật & Công nghệ", "Hà Nội / TP. Hồ Chí Minh", "Thực tập", "5 - 8 Triệu VNĐ",
                    5, "30/12/2026", "Đang tuyển", 65,
                    "Tham gia trực tiếp vào các module thực tế của hệ thống ATS dưới sự kèm cặp 1-1 từ các Senior Engineers. Được đào tạo bài bản về Java Spring Boot, ReactJS, Docker và ứng dụng AI trong doanh nghiệp.",
                    "Sinh viên năm 3, năm 4 hoặc mới tốt nghiệp chuyên ngành CNTT/Khoa học máy tính. Nắm vững lập trình hướng đối tượng OOP, Java/JavaScript cơ bản, ham học hỏi và có tư duy logic tốt.",
                    "Trợ cấp thực tập 5 - 8M + phụ cấp ăn trưa, hỗ trợ con dấu xác nhận thực tập và tài liệu làm đồ án tốt nghiệp, cơ hội trở thành nhân viên chính thức sau 3 tháng.", 0
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

        log.info("Dữ liệu khởi tạo thành công! Tổng số tin tuyển dụng hiện có: {}", jobPostingRepository.count());
    }
}