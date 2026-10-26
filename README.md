# TalentFlow — Recruitment Portal & ATS (Đồ Án Kì IV)

> Hệ thống Quản lý Quy trình Tuyển dụng & Phỏng vấn Nhân sự (Single-Tenant ATS)  
> **Tech Stack:** Spring Boot 3.3.4 (Java 21) + Thymeleaf + Tailwind CSS + Flutter REST APIs

---

## 📌 1. Giới thiệu tổng quan
Dự án được xây dựng nhằm số hóa toàn bộ vòng đời tuyển dụng của doanh nghiệp:
- **Yêu cầu tuyển dụng (Requisitions)** ➔ **Tin tuyển dụng (Job Postings)** ➔ **Hồ sơ ứng viên (Candidates & CV)** ➔ **Bài test năng lực (Assessments & Inbound Webhook)** ➔ **Lịch phỏng vấn & Scorecard** ➔ **Thư mời nhận việc (Offers)** ➔ **Tiếp nhận nhân sự mới (Onboarding)**.
- Đầy đủ 2 phân hệ:
  - **Back-office (Nội bộ):** Bảng điều khiển KPI, Pipeline Kanban kéo–thả, quản trị vai trò RBAC, xuất báo cáo Excel, mẫu email tự động, nhật ký hệ thống Audit Log.
  - **Front-office (Công khai):** Career Site lọc theo ngành nghề & khu vực, cổng nộp CV mở (Talent Network), tra cứu tiến trình hồ sơ thời gian thực, trang làm bài test bảo mật trực tuyến có đồng hồ đếm ngược.

---

## 🚀 2. Cấu trúc thư mục dự án
```text
QuanLyTuyenDung/
├── docs/                        # Tài liệu đặc tả yêu cầu chi tiết (01 -> 06)
│   ├── 01-requirements.md       # Đặc tả yêu cầu chức năng & phi chức năng (FR & NFR)
│   ├── 02-workflows.md          # Luồng nghiệp vụ end-to-end & State machine
│   ├── 03-pages.md              # Bản đồ sitemap & Wireframe từng màn hình
│   ├── 04-data-model.md         # Mô hình dữ liệu ERD & Data dictionary
│   ├── 05-architecture.md       # Kiến trúc hệ thống, bảo mật & API spec
│   └── 06-roadmap.md            # Lộ trình triển khai & phân chia giai đoạn
├── recruitment_prototype.html   # File Prototype All-in-One xem trực tiếp trên trình duyệt
├── src/
│   ├── main/
│   │   ├── java/com/example/quanlytuyendung/
│   │   │   ├── controllers/     # RecruitmentController & CareersController
│   │   │   └── QuanLyTuyenDungApplication.java
│   │   └── resources/
│   │       ├── static/css/      # recruitment.css
│   │       └── templates/recruitment/ # 18 giao diện Thymeleaf hoàn chỉnh
└── pom.xml                      # Cấu hình Maven Spring Boot 3.3.4 (Java 21)
```

---

## 💻 3. Hướng dẫn khởi chạy ứng dụng

### Chạy bằng Spring Tool Suite (Eclipse STS) hoặc IntelliJ IDEA
1. Mở IDE, chọn **Import Existing Maven Project** và trỏ đến thư mục `QuanLyTuyenDung`.
2. Kiểm tra JDK sử dụng là **Java 21**.
3. Chạy file `QuanLyTuyenDungApplication.java` (`Run As Spring Boot App`).
4. Truy cập các đường dẫn:
   - **Cổng tuyển dụng công khai:** `http://localhost:8080/careers`
   - **Tra cứu hồ sơ ứng tuyển:** `http://localhost:8080/careers/track?query=APP-2026-101`
   - **Trang thi năng lực bảo mật:** `http://localhost:8080/careers/assessment/TEST-SEC-8821`
   - **Portal Quản lý ATS (HR):** `http://localhost:8080/recruitment`
   - **Xuất dữ liệu Excel (.csv):** `http://localhost:8080/recruitment/export-excel`

### Mở Prototype nhanh (Không cần server)
- Nhấp đúp chuột vào file `recruitment_prototype.html` để mở trực tiếp trên Chrome / Edge / Cốc Cốc.
