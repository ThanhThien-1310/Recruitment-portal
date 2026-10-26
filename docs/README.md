# Recruitment Portal — Bản kế hoạch dự án (v0.1)

> Portal quản lý quy trình tuyển dụng nhân sự nội bộ (single-tenant)
> Stack: **Next.js + NestJS + PostgreSQL**
> Tài liệu này là bản thiết kế trước khi code. Đọc theo thứ tự từ 01 → 06.

---

## 1. Tóm tắt trong 1 phút

| Hạng mục | Nội dung |
|---|---|
| Mục tiêu | Số hóa toàn bộ vòng đời tuyển dụng: nhu cầu → tin đăng → CV → phỏng vấn → offer → nhận việc |
| Người dùng | Ứng viên, Recruiter, Hiring Manager, Interviewer, Ban lãnh đạo, Admin |
| Mô hình | Nội bộ 1 công ty, nhiều phòng ban, phân quyền theo vai trò + phòng ban |
| Backend | NestJS + PostgreSQL (Prisma) + Redis + BullMQ |
| Frontend | Next.js (App Router) + TypeScript + Tailwind + shadcn/ui |
| Lưu file | MinIO (self-host) hoặc S3, ký URL tạm thời |
| Email | SMTP nội bộ hoặc SendGrid/Resend |
| Triển khai | Docker Compose trên VPS/on-prem |
| Thời gian dự kiến | MVP ~7 tuần với 2 dev (BE + FE) + 0.5 BA/QC; ~10–12 tuần nếu chỉ 1 dev full-stack |

## 2. Danh sách phân hệ trên portal

**Back-office (nội bộ)**

1. Dashboard
2. Yêu cầu tuyển dụng (Requisitions)
3. Tin tuyển dụng & Kênh đăng (Job Postings & Sources)
4. Ứng viên & Kho CV mở (Candidates & Talent Pool / AI Matching)
5. Pipeline tuyển dụng (Kanban)
6. Bài test & Đánh giá năng lực (Assessments: Take-home / Online test, nộp qua Web hoặc Email)
7. Lịch & đánh giá phỏng vấn (Interviews & Scorecards)
8. Offer & phê duyệt (Offers)
9. Onboarding
10. Báo cáo (Reports)
11. Cài đặt (Users, Roles, Departments, Categories, Locations, Pipeline, Email, Sources, Questions Bank, Audit log)

**Front-office (công khai)**

12. Career site: danh sách việc làm (lọc theo Ngành nghề & Khu vực), chi tiết JD, form ứng tuyển kèm câu hỏi sàng lọc (Pre-screening questions), cổng nộp hồ sơ mở (Talent Network), tra cứu trạng thái hồ sơ.
13. Cổng làm / nộp bài test bảo mật cho ứng viên (Assessment Portal qua Secure Token).

## 3. Cấu trúc tài liệu

| File | Nội dung |
|---|---|
| [01-requirements.md](01-requirements.md) | Phạm vi, actors, yêu cầu chức năng (FR), phi chức năng (NFR), quy tắc nghiệp vụ, tiêu chí nghiệm thu |
| [02-workflows.md](02-workflows.md) | Luồng nghiệp vụ end-to-end, state machine từng đối tượng, ma trận chuyển trạng thái, email trigger, SLA |
| [03-pages.md](03-pages.md) | Sitemap, wireframe, nội dung chi tiết từng trang/màn hình, design system |
| [04-data-model.md](04-data-model.md) | ERD, chi tiết entity/field, enum, index, dữ liệu mẫu |
| [05-architecture.md](05-architecture.md) | Kiến trúc hệ thống, cấu trúc repo, API, auth/RBAC, bảo mật, deploy, testing |
| [06-roadmap.md](06-roadmap.md) | Chia phase/sprint, backlog ưu tiên, ước lượng, rủi ro, checklist khởi động |

## 4. Quyết định đã chốt

- [x] Stack: Next.js + NestJS + PostgreSQL
- [x] Single-tenant (1 công ty)
- [x] Có career site công khai cho ứng viên (kèm cổng nộp hồ sơ mở Talent Network)
- [x] Phân quyền RBAC theo vai trò + phạm vi phòng ban
- [x] Tiếng Việt là ngôn ngữ chính, kiến trúc sẵn sàng i18n EN
- [x] Email: Dùng **Resend** (3,000 mail/tháng miễn phí, hỗ trợ Inbound Webhook nhận bài test qua reply email) + **MailHog** cho môi trường dev
- [x] AI: Dùng **Google Gemini Flash API** để parse CV tự động bóc tách thông tin & matching gợi ý ứng viên phù hợp với tin tuyển dụng
- [x] Tính năng sàng lọc: Hỗ trợ **Câu hỏi sàng lọc (Pre-screening/Knock-out questions)** bắt buộc khi nộp CV
- [x] Đánh giá năng lực: Hỗ trợ **Giao bài test riêng (Private Assessment)** cho phép ứng viên nộp bài qua Web hoặc bấm Reply Email (tự động nhận diện bài nộp & lấy timestamp email chống tính quá hạn oan)
- [x] Trả kết quả phỏng vấn 1-click: Nhấn nút **Pass** hoặc **Deny** sẽ tự động gửi email cho ứng viên và đồng bộ trạng thái hồ sơ tức thì ở cả 2 phía (Kanban nội bộ và Cổng tra cứu ứng viên)
- [x] Dữ liệu danh mục: Chuẩn hóa Ngành nghề (Job Category) và Khu vực (Location) với Composite Index để tìm kiếm cực nhanh, không lo nặng dữ liệu

## 5. Quyết định còn mở (cần chốt trước Phase 1)

- [ ] Tên thương hiệu portal & domain chính thức
- [ ] Có cần đăng nhập Google/Microsoft SSO không (hay dùng tài khoản cấp phát mật khẩu trước)?
- [ ] Lưu file trên server nội bộ (MinIO) hay cloud (AWS S3)?
- [ ] Có cần tích hợp đồng bộ 2 chiều với Google Calendar/Outlook ngay từ MVP?
- [ ] Quy trình phê duyệt thực tế: mấy cấp, ai duyệt?
- [ ] Số vòng phỏng vấn chuẩn của công ty?

## 6. Cách dùng tài liệu

1. Đọc `01` để xác nhận **làm gì / không làm gì**.
2. Đọc `02` để xác nhận **luồng chạy đúng nghiệp vụ thực tế**.
3. Duyệt `03` (wireframe + nội dung trang) — đây là phần bạn cảm nhận rõ nhất sản phẩm.
4. `04` + `05` dành cho triển khai kỹ thuật.
5. `06` để biết **thứ tự làm** và mốc bàn giao.
