# 06 — Lộ trình triển khai

## 1. Nguyên tắc

- **Làm theo luồng nghiệp vụ, không theo màn hình rời rạc.** Mỗi sprint phải có một luồng chạy được từ đầu đến cuối.
- **MVP mỏng nhưng thật**: ưu tiên dữ liệu thật, phân quyền thật, email thật ở môi trường staging.
- **Demo cuối mỗi sprint** cho HR/Hiring Manager để chỉnh sớm.
- **Không chờ đủ tính năng mới go-live**: chạy song song Excel trong 2 tuần đầu.
- Mọi tính năng phải có: API + UI + phân quyền + audit + trạng thái rỗng/lỗi.

## 2. Đội hình giả định

| Vai trò | Số lượng | Trách nhiệm |
|---|---|---|
| Backend dev (NestJS/Prisma) | 1 | API, DB, queue, bảo mật |
| Frontend dev (Next.js) | 1 | UI, UX, career site |
| BA/QC (bán thời gian) | 0.5 | Chốt yêu cầu, viết test case, UAT |
| HR key user | 0.2 | Phản hồi nghiệp vụ, test |

> Nếu chỉ có **1 dev full-stack**: giữ nguyên thứ tự nhưng giãn ra ~10–12 tuần.

## 3. Ước lượng theo module

| Module | Người-ngày | Ghi chú |
|---|---|---|
| Nền tảng: repo, Docker, auth, RBAC, layout, CI | 8 | Làm trước, ảnh hưởng toàn bộ |
| Người dùng / vai trò / phòng ban / ngành nghề / khu vực | 6 | CRUD + ma trận quyền + taxonomy |
| Yêu cầu tuyển dụng + phê duyệt | 8 | Wizard, state machine, thông báo |
| Tin tuyển dụng + câu hỏi sàng lọc + career site | 10 | Rich text, publish, SEO, knock-out questions, apply |
| Kho ứng viên + Talent Network + AI CV parse & matching | 12 | Gemini Flash parse CV, gợi ý matching, chống trùng |
| Pipeline Kanban + lịch sử + bulk | 8 | dnd-kit, drawer, SLA |
| Bài test & đánh giá (Assessments: Web + Email reply webhook) | 7 | Secure token, webhook parse email, kiểm tra deadline |
| Phỏng vấn + lịch + scorecard | 10 | Calendar, .ics, tổng hợp điểm |
| Offer + phê duyệt + PDF | 7 | Template, luồng duyệt |
| Onboarding | 4 | Checklist, template |
| Dashboard + báo cáo + export | 8 | 6 báo cáo, cache |
| Thông báo + email template (Resend) | 6 | 18 trigger, webhook inbound |
| Audit log + hardening + UAT fixes | 8 | Bảo mật, hiệu năng, bug |
| **Tổng** | **~102 người-ngày** | ~7–8 tuần với 2 dev song song |

## 4. Các phase & sprint

### Phase 0 — Nền móng (tuần 1)

**Mục tiêu:** có khung chạy được, đăng nhập, phân quyền, DB schema, CI.

| Việc | Kết quả |
|---|---|
| Khởi tạo monorepo, lint/format, tsconfig, Tailwind, shadcn | `npm run dev` chạy web + api |
| Docker Compose: postgres, redis, minio, mailhog | Môi trường dev 1 lệnh |
| Prisma schema đầy đủ (bao gồm Category, Location, Screening, Assessment) + migration + seed | DB có dữ liệu mẫu |
| Auth: login, refresh, logout, forgot/reset, invite | Đăng nhập được |
| RBAC: role/permission, guard, scope phòng ban | API chặn đúng quyền |
| Layout: sidebar, topbar, breadcrumb, toast, error pages | Khung UI |
| CI: lint, typecheck, test, build | PR phải xanh |
| Swagger + healthcheck | `/api/docs`, `/health` |

**Demo:** đăng nhập bằng 3 vai trò khác nhau, thấy menu khác nhau; gọi API trái quyền bị 403.

---

### Phase 1 — Tuyển dụng cốt lõi & Sàng lọc (tuần 2–3)

**Mục tiêu:** từ nhu cầu → tin đăng → ứng viên → sàng lọc & pipeline.

**Sprint 1 — Yêu cầu, tin đăng & câu hỏi sàng lọc**
- Danh mục Ngành nghề (Job Categories) & Khu vực (Locations) có Composite Index.
- Requisition: danh sách, filter, wizard 3 bước, chi tiết tabs, luồng duyệt.
- Job posting: soạn JD, cấu hình bộ câu hỏi sàng lọc (Pre-screening) kèm điều kiện loại trực tiếp (Knock-out).
- Career site: danh sách việc làm lọc theo Ngành/Khu vực, chi tiết JD (SSR/SEO).
- Public apply: form apply + trả lời câu hỏi sàng lọc + upload CV + consent, email xác nhận nộp hồ sơ.

**Sprint 2 — Ứng viên, Talent Network & Pipeline**
- Cổng nộp hồ sơ mở (Talent Network / General Application) cho ứng viên gửi CV tự do vào Talent Pool.
- Tích hợp Gemini Flash AI: tự động bóc tách kỹ năng, kinh nghiệm từ file CV và cập nhật tag hồ sơ.
- AI Matching: gợi ý top ứng viên tiềm năng trong kho cho tin tuyển dụng mới.
- Pipeline Kanban: kéo–thả, hiển thị nhãn knock-out đạt/trượt, lý do từ chối, lịch sử chuyển bước.
- Dashboard phiên bản 1: KPI + funnel + việc cần xử lý.

**Demo:** tạo yêu cầu → duyệt → publish tin có câu hỏi sàng lọc → ứng viên apply / nộp CV mở → AI tự parse CV → hồ sơ vào Kanban có cờ knock-out → kéo bước.

---

### Phase 2 — Bài test, Phỏng vấn & Offer (tuần 4–5)

**Sprint 3 — Bài test & Đánh giá năng lực (Assessments)**
- Giao bài test: Take-home assignment hoặc Online quiz kèm deadline.
- Sinh link web làm bài bảo mật (Secure Token) và địa chỉ Reply-To định danh riêng.
- Cổng nộp bài trên Web (/assessment/[token]) và Webhook nhận bài nộp qua Email reply (Resend Inbound).
- Cơ chế đối soát timestamp email chống tính trễ hạn oan, tự động bóc tách file vào MinIO.
- Giao diện chấm điểm bài test cho Recruiter / Reviewer.

**Sprint 4 — Phỏng vấn, Offer & thông báo**
- Đặt lịch PV: modal, kiểm tra trùng lịch, email + .ics, nhắc lịch trước 24h.
- Scorecard: template, chấm điểm, tổng hợp điểm trung bình.
- Tạo offer, duyệt offer theo ngưỡng, sinh PDF offer letter, ghi nhận accept/decline.
- Accept offer → tự tạo onboarding plan + application `HIRED`.
- Trung tâm thông báo + 18 trigger email Resend.

**Demo:** giao bài test → ứng viên reply email nộp bài → hệ thống tự nhận đúng hạn → chấm điểm pass → đặt lịch PV → chấm scorecard → duyệt offer → accept.

---

### Phase 3 — Báo cáo, cài đặt, hoàn thiện (tuần 6–7)

- Dashboard đầy đủ theo vai trò.
- 6 báo cáo + export Excel/CSV + ghim dashboard.
- Onboarding: danh sách, checklist, template, nhắc hạn.
- Settings: users, roles matrix, departments, categories, locations, screening questions bank, pipeline, sources, reject reasons, email templates, audit log.
- Audit log + trang xem chi tiết thay đổi.
- Import/export ứng viên Excel.
- Trạng thái rỗng/lỗi/skeleton toàn bộ; responsive mobile.
- Seed dữ liệu demo đầy đủ.

**Demo:** chạy báo cáo, export, cấu hình thêm 1 stage mới và thấy thay đổi trong Kanban; xem audit log.

---

### Phase 4 — UAT & go-live (tuần 8)

| Việc | Kết quả |
|---|---|
| Viết test case theo AC (01-requirements §7) | Bộ test UAT |
| UAT với HR + Hiring Manager (3–5 người, 2 ngày) | Danh sách bug ưu tiên |
| Sửa bug P0/P1, chấp nhận P2 vào backlog | Bản release candidate |
| Load test nhẹ (100 user, 10k ứng viên) | Báo cáo hiệu năng |
| Kiểm tra bảo mật: phân quyền, upload, rate limit, audit | Checklist bảo mật |
| Backup/restore thử nghiệm | Xác nhận RTO/RPO |
| Deploy production + SSL + domain | Hệ thống chạy |
| Đào tạo người dùng (2 buổi) + tài liệu hướng dẫn | HR tự vận hành |
| Chạy song song Excel 2 tuần, hỗ trợ trực tiếp | Chuyển đổi an toàn |

**Go-live checklist**
- [ ] Domain + HTTPS hoạt động
- [ ] Email gửi thật tới Gmail/Outlook, không vào spam (SPF/DKIM/DMARC)
- [ ] Backup tự động + đã test restore
- [ ] Tài khoản admin đầu tiên đổi mật khẩu mạnh
- [ ] Dữ liệu thật được import (nếu cần)
- [ ] Thông báo nội bộ + hướng dẫn sử dụng
- [ ] Kênh hỗ trợ (chat/nhóm) trong 2 tuần đầu
- [ ] Log + cảnh báo hoạt động
- [ ] Chính sách bảo mật dữ liệu cá nhân được duyệt

## 5. Backlog ưu tiên (MoSCoW)

| Ưu tiên | Hạng mục |
|---|---|
| **Must** | Auth/RBAC · Requisition + duyệt · Job posting + career site + apply · Candidate + CV · Kanban · Lịch PV + scorecard · Offer + duyệt · Dashboard · Thông báo/email · Cài đặt cơ bản · Audit |
| **Should** | Báo cáo đầy đủ · Onboarding · Import/export · Talent pool · Saved views · SLA cảnh báo · Offer PDF · Lịch sử ứng viên |
| **Could** | AI parse CV · Đồng bộ Google Calendar/Outlook · SSO · Referral · Đa ngôn ngữ EN · Báo cáo chi phí · 2FA · Mobile app |
| **Won't (nay)** | HRM/payroll · e-sign hợp đồng · đa tenant · đánh giá hiệu suất sau vào làm |

## 6. Rủi ro & giảm thiểu

| # | Rủi ro | Ảnh hưởng | Giảm thiểu |
|---|---|---|---|
| 1 | Yêu cầu thay đổi liên tục | Trễ tiến độ | Demo hằng tuần, chốt scope theo MoSCoW, change log |
| 2 | Dữ liệu CV cũ không sạch | Khó import | Chuẩn hóa Excel trước, cho phép import có báo lỗi từng dòng |
| 3 | Email vào spam | Mất thông báo | Cấu hình SPF/DKIM/DMARC, dùng domain riêng, test trước go-live |
| 4 | Phân quyền sai gây lộ CV | Rủi ro pháp lý | Test case phân quyền, review code, audit log, nguyên tắc least privilege |
| 5 | Hiệu năng khi dữ liệu lớn | Chậm | Index, phân trang, cache, load test sớm |
| 6 | Người dùng không chịu chuyển từ Excel | Dự án thất bại | Cho key user tham gia từ sprint 1, chạy song song, đào tạo |
| 7 | Thiếu người test/UAT | Bug lọt | BA/QC viết test case sớm, lịch UAT cố định |
| 8 | Một dev nghỉ giữa chừng | Gián đoạn | Tài liệu hóa, code review, không silo kiến thức |
| 9 | Tích hợp calendar/SSO bị chặn | Trễ phase sau | Để ngoài MVP, thiết kế interface sẵn |
| 10 | Dữ liệu cá nhân chưa có consent | Không tuân thủ NĐ13 | Bắt buộc checkbox consent, lưu `consentAt`, có quy trình xóa |

## 7. Câu hỏi mở cần chốt (trước Sprint 1)

1. Quy trình duyệt requisition thực tế: mấy cấp, ai duyệt, có cần duyệt headcount riêng không?
2. Ngưỡng lương nào cần BOD duyệt offer?
3. Danh sách phòng ban, chức danh, nguồn tuyển dụng hiện tại?
4. Các vòng phỏng vấn chuẩn và tiêu chí đánh giá từng vòng?
5. Mẫu email/offer letter hiện có?
6. Có cần chuyển dữ liệu ứng viên từ Excel/TopCV không? Khối lượng bao nhiêu?
7. Ai là admin hệ thống? Bao nhiêu user dự kiến?
8. Có yêu cầu lưu trữ trên hạ tầng nội bộ (on-prem) không?
9. Ngôn ngữ: chỉ tiếng Việt hay cần tiếng Anh ngay?
10. Tên portal/domain/logo?

## 8. Definition of Done

Một hạng mục được coi là xong khi:

- [ ] Đúng AC, đã demo cho HR/BA
- [ ] API có validate + phân quyền + audit cho hành động quan trọng
- [ ] UI có đủ trạng thái loading / rỗng / lỗi / không quyền
- [ ] Responsive ở 375px, 768px, 1440px
- [ ] Unit test cho logic nghiệp vụ; e2e cho luồng chính
- [ ] Không lỗi TypeScript, lint sạch, build production thành công
- [ ] Swagger cập nhật
- [ ] Không log dữ liệu nhạy cảm
- [ ] Đã review code bởi người còn lại
- [ ] Tài liệu user guide cập nhật (nếu là tính năng người dùng)

## 9. Kịch bản UAT chính (rút gọn)

| # | Kịch bản | Kỳ vọng |
|---|---|---|
| UAT-01 | Recruiter tạo requisition thiếu trường bắt buộc | Báo lỗi rõ, không lưu |
| UAT-02 | Hiring Manager duyệt requisition | Trạng thái đổi, người tạo nhận thông báo |
| UAT-03 | Publish tin khi requisition chưa duyệt | Bị chặn kèm thông báo |
| UAT-04 | Ứng viên apply trên mobile | Thành công, nhận email có mã hồ sơ |
| UAT-05 | Apply trùng email | Cảnh báo/tạo application mới theo quy tắc |
| UAT-06 | Kéo ứng viên sang bước PV | Mở form đặt lịch, ghi lịch sử |
| UAT-07 | Từ chối không chọn lý do | Không cho lưu |
| UAT-08 | Đặt lịch trùng giờ interviewer | Cảnh báo, không tạo trùng |
| UAT-09 | Interviewer chấm scorecard | Lưu, HR thấy điểm TB |
| UAT-10 | Offer vượt ngưỡng | Bắt buộc BOD duyệt |
| UAT-11 | Ứng viên accept offer | Tạo onboarding, requisition tăng filledCount |
| UAT-12 | Recruiter xem CV của phòng khác | Bị chặn 403 |
| UAT-13 | Export báo cáo | File Excel đúng số liệu |
| UAT-14 | Sửa stage trong cài đặt | Kanban cập nhật đúng thứ tự |

## 10. Checklist khởi động dự án

- [ ] Chốt 10 câu hỏi mở ở §7
- [ ] Chốt tên, logo, màu thương hiệu, domain
- [ ] Xác nhận đội hình và lịch họp (weekly demo + daily 15')
- [ ] Tạo repo Git, phân quyền, branch protection
- [ ] Tạo môi trường dev/staging, tài khoản email test
- [ ] Chốt mẫu Excel dữ liệu cũ để import
- [ ] Lên lịch UAT và người tham gia
- [ ] Duyệt bộ tài liệu này (01 → 06) làm baseline v1.0

## 11. Sau MVP (phase nâng cao, tuần 8–12)

| Hạng mục | Giá trị |
|---|---|
| AI parse CV + gợi ý ứng viên | Giảm nhập liệu, tăng tốc sàng lọc |
| Đồng bộ Google Calendar/Outlook | Tránh nhập lịch 2 lần |
| SSO Google/Microsoft | Bảo mật và tiện dụng |
| Referral portal cho nhân viên | Tăng nguồn ứng viên chất lượng |
| Ứng viên tự xác nhận/đổi lịch PV | Giảm việc cho HR |
| Báo cáo chi phí tuyển dụng | Tối ưu ngân sách |
| Đa ngôn ngữ EN | Tuyển ứng viên nước ngoài |
| Mobile PWA + push | Duyệt nhanh trên điện thoại |
| E-sign offer | Khép kín quy trình |
