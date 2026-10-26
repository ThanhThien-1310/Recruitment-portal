# 01 — Yêu cầu dự án

## 1. Bối cảnh & mục tiêu

Công ty tuyển dụng qua nhiều kênh (email, TopCV, LinkedIn, Facebook, giới thiệu nội bộ) và theo dõi bằng Excel. Hệ quả:

- Không biết chính xác một ứng viên đang ở bước nào.
- Trùng lặp hồ sơ, mất CV, không lưu được lý do từ chối.
- Hiring Manager và HR trao đổi qua chat/email, không có lịch sử.
- Không đo được hiệu quả kênh, thời gian tuyển, chi phí tuyển.
- Ứng viên không nhận được phản hồi → giảm trải nghiệm thương hiệu tuyển dụng.

**Mục tiêu:** một portal duy nhất quản lý toàn bộ pipeline, có phân quyền, có báo cáo, có cổng ứng viên.

### KPI thành công (đo sau 3 tháng vận hành)

| KPI | Mục tiêu |
|---|---|
| Tỷ lệ hồ sơ được cập nhật trạng thái trong 24h | ≥ 95% |
| Giảm thời gian tổng hợp báo cáo tuyển dụng | từ 4h → < 15 phút/tháng |
| Time-to-hire | giảm 15% |
| Tỷ lệ ứng viên được phản hồi kết quả | 100% |
| Tỷ lệ dùng portal thay Excel cho job mới | 100% |

## 2. Phạm vi

### 2.1 Trong phạm vi

- Quản lý yêu cầu tuyển dụng và luồng phê duyệt.
- Đăng tin, quản lý kênh, nguồn ứng viên, danh mục Ngành nghề và Khu vực/Địa điểm.
- Kho ứng viên, CV, tìm kiếm/lọc, nộp hồ sơ mở (Talent Network) và AI parse CV & Matching gợi ý ứng viên.
- Form nộp hồ sơ kèm bộ câu hỏi sàng lọc (Pre-screening / Knock-out questions).
- Pipeline Kanban nhiều vòng, lịch sử chuyển bước.
- Giao bài test & đánh giá năng lực (Assessments), hỗ trợ nộp trực tiếp qua Web hoặc nhận bài tự động qua Email reply (Inbound Webhook).
- Đặt lịch phỏng vấn, phân công, scorecard, tổng hợp điểm.
- Offer và phê duyệt offer.
- Onboarding cơ bản (checklist).
- Dashboard, báo cáo, export Excel.
- Career site công khai (lọc theo ngành/khu vực) + cổng nộp CV mở + tra cứu trạng thái hồ sơ + trang làm bài test bảo mật.
- Quản trị người dùng, vai trò, phòng ban, cấu hình quy trình, email template, audit log.
- Thông báo in-app + email tự động (Resend).

### 2.2 Ngoài phạm vi (phase sau hoặc không làm)

- Tính lương, chấm công, BHXH (thuộc HRM).
- Quản lý chi phí tuyển dụng chi tiết theo hóa đơn (chỉ ghi nhận cost-per-hire ước tính).
- Chữ ký số/e-sign hợp đồng lao động.
- Đánh giá hiệu suất sau khi vào làm.
- Mobile app native (dùng responsive web).
- Đa công ty/đa tenant.
- Tích hợp máy chấm công, ERP, Jira (có thể mở rộng qua API).

## 3. Actors & phân quyền

| Vai trò | Mô tả | Phạm vi dữ liệu |
|---|---|---|
| **Admin** | Quản trị hệ thống | Toàn bộ + cấu hình |
| **HR Manager** | Trưởng phòng nhân sự | Toàn bộ công ty, duyệt cấp 2 |
| **Recruiter** | Chuyên viên tuyển dụng | Job được giao + toàn bộ ứng viên liên quan |
| **Hiring Manager** | Trưởng bộ phận có nhu cầu | Job + ứng viên của phòng mình |
| **Interviewer** | Người phỏng vấn | Chỉ lịch PV được phân công + hồ sơ ứng viên đó |
| **BOD** | Ban lãnh đạo | Xem dashboard/báo cáo, duyệt offer/headcount |
| **Employee** | Nhân viên (referral) | Tạo referral, xem trạng thái referral của mình |
| **Candidate** | Ứng viên (public) | Chỉ hồ sơ của mình qua mã hồ sơ + email |

Ma trận quyền chi tiết: xem [02-workflows.md §7](02-workflows.md#7-ma-trận-quyền).

## 4. Yêu cầu chức năng (Functional Requirements)

Mức ưu tiên: **M** = Must (MVP), **S** = Should (phase 2), **C** = Could (phase 3).

### 4.1 Dashboard

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-DB-01 | Hiển thị KPI: số vị trí đang mở, tổng ứng viên active, PV trong tuần, offer chờ duyệt, time-to-hire TB | M |
| FR-DB-02 | Biểu đồ funnel theo vị trí/phòng ban/khoảng thời gian | M |
| FR-DB-03 | Danh sách PV sắp tới của tôi | M |
| FR-DB-04 | Cảnh báo hồ sơ quá SLA ở từng bước | S |
| FR-DB-05 | Hoạt động gần đây (audit feed) | S |

### 4.2 Yêu cầu tuyển dụng (Requisition)

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-RQ-01 | Tạo/sửa/nhân bản yêu cầu: mã, tên vị trí, phòng ban, số lượng, loại hợp đồng, địa điểm/hình thức làm việc, mức lương min–max, lý do tuyển, hạn chót | M |
| FR-RQ-02 | Gửi duyệt theo luồng cấu hình (Hiring Manager → HR Manager → BOD nếu lương > ngưỡng) | M |
| FR-RQ-03 | Người duyệt approve/reject kèm ý kiến; ghi log từng cấp | M |
| FR-RQ-04 | Trạng thái: Nháp → Chờ duyệt → Đã duyệt → Đang tuyển → Tạm dừng → Đóng/Hủy | M |
| FR-RQ-05 | Tự đóng khi tuyển đủ số lượng, hoặc đóng thủ công kèm lý do | M |
| FR-RQ-06 | Hiển thị tiến độ: đã tuyển / chỉ tiêu | M |
| FR-RQ-07 | Clone yêu cầu cũ | S |
| FR-RQ-08 | Đính kèm JD, file headcount | S |

### 4.3 Tin tuyển dụng & kênh

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-JP-01 | Tạo tin từ requisition, soạn JD (rich text), preview như career site | M |
| FR-JP-02 | Publish/unpublish lên career site bằng 1 click | M |
| FR-JP-03 | Quản lý danh mục nguồn/kênh (Career site, TopCV, LinkedIn, Facebook, Referral, Headhunt…) | M |
| FR-JP-04 | Gắn nguồn cho từng ứng viên, báo cáo hiệu quả nguồn | M |
| FR-JP-05 | Phân loại tin theo Danh mục Ngành nghề (Job Category) và Khu vực/Tỉnh thành (Location) | M |
| FR-JP-06 | Cấu hình bộ câu hỏi sàng lọc (Screening Questions) kèm điều kiện loại trực tiếp (Knock-out) cho từng tin | M |
| FR-JP-07 | Theo dõi lượt xem tin, số apply | S |
| FR-JP-08 | Tự động đóng tin khi hết hạn hoặc đủ chỉ tiêu | S |
| FR-JP-09 | Mẫu JD, thư viện câu hỏi sàng lọc dùng chung | S |

### 4.4 Ứng viên & kho CV

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-CD-01 | Tạo ứng viên thủ công hoặc từ form apply; bắt buộc họ tên, email, SĐT, nguồn | M |
| FR-CD-02 | Upload nhiều CV (PDF/DOCX), gắn vào hồ sơ, tải xuống có kiểm soát quyền | M |
| FR-CD-03 | Phát hiện trùng theo email/SĐT, cảnh báo khi tạo mới | M |
| FR-CD-04 | Tìm kiếm full-text theo tên/email/SĐT/kỹ năng; lọc theo trạng thái, ngành nghề, khu vực, nguồn, tag | M |
| FR-CD-05 | Hồ sơ ứng viên: thông tin cá nhân, học vấn, kinh nghiệm, kỹ năng, CV, câu trả lời sàng lọc, bài test, đánh giá | M |
| FR-CD-06 | Parse CV tự động bằng AI (Gemini Flash): trích xuất họ tên, kỹ năng, số năm kinh nghiệm, học vấn | M |
| FR-CD-07 | Kho hồ sơ mở (Talent Network / General Application): ứng viên nộp CV không cần chọn tin cụ thể | M |
| FR-CD-08 | AI Matching: tự động gợi ý top ứng viên tiềm năng phù hợp với tin tuyển dụng mới và ngược lại | S |
| FR-CD-09 | Tag và talent pool; lưu trữ ứng viên tiềm năng để tái sử dụng | S |
| FR-CD-10 | Merge hồ sơ trùng | S |
| FR-CD-11 | Import/export Excel | S |
| FR-CD-12 | Gửi email hàng loạt theo mẫu | S |
| FR-CD-13 | Tuân thủ xóa/ẩn dữ liệu cá nhân theo yêu cầu (Nghị định 13/2023/NĐ-CP) | S |

### 4.5 Pipeline

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-PL-01 | Kanban theo requisition, kéo–thả đổi bước | M |
| FR-PL-02 | Cấu hình bước mặc định: Sàng lọc → Bài test → PV chuyên môn → PV cuối → Offer → Nhận việc; nhánh Từ chối/Talent pool | M |
| FR-PL-03 | Bắt buộc nhập lý do khi loại ứng viên (danh mục lý do cấu hình) | M |
| FR-PL-04 | Ghi lịch sử: ai chuyển, khi nào, từ bước nào → bước nào, ghi chú | M |
| FR-PL-05 | Chuyển hàng loạt (bulk move/reject) | S |
| FR-PL-06 | Card hiển thị: tên, vị trí, nguồn, câu hỏi knock-out (đạt/không), điểm test, số ngày ở bước, avatar phụ trách | M |
| FR-PL-07 | SLA theo bước, tô đỏ khi quá hạn | S |
| FR-PL-08 | Chế độ xem danh sách + filter nâng cao | S |

### 4.6 Bài test & Đánh giá năng lực (Assessments)

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-AS-01 | Giao bài test riêng cho ứng viên: chọn loại (Take-home tải file hoặc Online quiz), thiết lập hạn nộp (deadline), đính kèm đề bài | M |
| FR-AS-02 | Sinh link truy cập bảo mật một lần (Secure Token) và địa chỉ Reply-To định danh riêng cho từng bài test | M |
| FR-AS-03 | Tự động gửi email giao bài test kèm link làm bài và hướng dẫn nộp bài (qua Web hoặc Reply email) | M |
| FR-AS-04 | Tiếp nhận bài nộp trực tiếp trên Web: upload file bài giải (PDF, ZIP, DOCX) hoặc nộp đường link (Github, Figma) | M |
| FR-AS-05 | Tiếp nhận bài nộp tự động qua Reply Email: Inbound Webhook bóc tách file đính kèm, lấy timestamp email kiểm tra deadline | M |
| FR-AS-06 | Tự động cập nhật trạng thái bài test: Đã nộp đúng hạn (`SUBMITTED`) hoặc Nộp trễ (`SUBMITTED_LATE`), hủy cảnh báo quá hạn oan | M |
| FR-AS-07 | Nhà tuyển dụng / Chuyên gia chấm bài test: nhập điểm, nhận xét, phê duyệt hoặc yêu cầu bổ sung | M |
| FR-AS-08 | Thư viện đề thi mẫu và ngân hàng câu hỏi trắc nghiệm đánh giá năng lực | S |

### 4.7 Lịch & đánh giá phỏng vấn

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-IV-01 | Tạo lịch PV: ứng viên, vòng, thời gian, thời lượng, địa điểm/meeting link, người PV | M |
| FR-IV-02 | Kiểm tra trùng lịch của interviewer, cảnh báo | M |
| FR-IV-03 | Gửi email mời ứng viên + interviewer, có file .ics | M |
| FR-IV-04 | Ứng viên xác nhận tham dự / xin đổi lịch qua link | S |
| FR-IV-05 | Scorecard: tiêu chí + thang điểm + nhận xét, mỗi interviewer chấm riêng | M |
| FR-IV-06 | Tổng hợp điểm trung bình, hiển thị cho Hiring Manager/HR | M |
| FR-IV-07 | Calendar view ngày/tuần/tháng + list view | M |
| FR-IV-08 | Nhắc lịch trước 24h/1h | S |
| FR-IV-09 | Đồng bộ Google Calendar/Outlook | C |
| FR-IV-10 | Đính kèm tài liệu, câu hỏi phỏng vấn theo vòng | S |
| FR-IV-11 | Nút 1-click trả kết quả PV ("Pass" / "Deny"): Tự động gửi email kết quả cho ứng viên và đồng bộ tức thì lên hồ sơ 2 phía (cột Kanban nội bộ & Cổng tra cứu ứng viên) | M |

### 4.8 Offer

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-OF-01 | Tạo offer từ ứng viên: chức danh, lương, phụ cấp, thưởng, ngày bắt đầu, thời hạn phản hồi | M |
| FR-OF-02 | Luồng duyệt offer (HR Manager → BOD nếu vượt ngưỡng) | M |
| FR-OF-03 | Sinh offer letter từ template, xuất PDF | S |
| FR-OF-04 | Trạng thái: Nháp → Chờ duyệt → Đã duyệt → Đã gửi → Chấp nhận/Từ chối/Hết hạn | M |
| FR-OF-05 | Ghi nhận lý do từ chối offer | M |
| FR-OF-06 | Khi accept → tự tạo onboarding + chuyển pipeline sang "Nhận việc" | M |

### 4.9 Onboarding

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-ON-01 | Tạo checklist onboarding từ template theo phòng ban/vị trí | S |
| FR-ON-02 | Phân công task cho HR/IT/Quản lý trực tiếp, deadline | S |
| FR-ON-03 | Theo dõi tiến độ % hoàn thành | S |
| FR-ON-04 | Lưu giấy tờ, hợp đồng (upload) | C |

### 4.10 Báo cáo

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-RP-01 | Funnel chuyển đổi theo vị trí/phòng ban/thời gian | M |
| FR-RP-02 | Time-to-hire, time-to-fill, time per stage | M |
| FR-RP-03 | Hiệu quả nguồn ứng viên (số CV, tỷ lệ pass, số hired) | M |
| FR-RP-04 | Năng suất recruiter, Hiring Manager | S |
| FR-RP-05 | Tỷ lệ offer accepted, lý do từ chối | S |
| FR-RP-06 | SLA vi phạm theo bước | S |
| FR-RP-07 | Export Excel/CSV, lọc theo khoảng thời gian | M |
| FR-RP-08 | Báo cáo chi phí tuyển dụng | C |

### 4.11 Quản trị

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-AD-01 | CRUD người dùng, gán vai trò + phòng ban, khóa tài khoản | M |
| FR-AD-02 | CRUD vai trò và ma trận quyền | M |
| FR-AD-03 | CRUD phòng ban (dạng cây) | M |
| FR-AD-04 | CRUD danh mục Ngành nghề tuyển dụng (Job Categories) và Khu vực / Địa điểm (Locations) | M |
| FR-AD-05 | Quản lý ngân hàng Câu hỏi sàng lọc & Đề thi đánh giá năng lực | M |
| FR-AD-06 | Cấu hình pipeline stage + SLA | M |
| FR-AD-07 | Cấu hình lý do từ chối, nguồn ứng viên | M |
| FR-AD-08 | Mẫu email + biến động, bật/tắt thông báo | M |
| FR-AD-09 | Audit log: ai làm gì, khi nào, đối tượng nào | M |
| FR-AD-10 | Cấu hình ngưỡng duyệt (lương/headcount) | S |
| FR-AD-11 | Cấu hình thương hiệu công ty cho career site | S |
| FR-AD-12 | Quản lý file / dung lượng lưu trữ | C |

### 4.12 Career site & cổng ứng viên

| ID | Yêu cầu | Ưu tiên |
|---|---|---|
| FR-CS-01 | Trang danh sách việc làm công khai, tìm kiếm/lọc theo Ngành nghề, Khu vực (Tỉnh/Thành), loại hình | M |
| FR-CS-02 | Trang chi tiết JD, nút Apply | M |
| FR-CS-03 | Form apply: thông tin cá nhân + upload CV + trả lời câu hỏi sàng lọc (Pre-screening questions) + consent dữ liệu | M |
| FR-CS-04 | Cổng nộp hồ sơ mở (Talent Network / General Application) cho ứng viên tự do nộp CV vào Talent Pool | M |
| FR-CS-05 | Cổng làm / nộp bài test bảo mật (Secure Token): cho phép tải đề, làm bài online hoặc upload bài giải trực tiếp | M |
| FR-CS-06 | Email xác nhận đã nhận hồ sơ + mã hồ sơ | M |
| FR-CS-07 | Tra cứu trạng thái bằng email + mã hồ sơ | S |
| FR-CS-08 | Trang giới thiệu công ty, phúc lợi | S |
| FR-CS-09 | Ứng viên xác nhận/đổi lịch PV | S |
| FR-CS-10 | Referral form cho nhân viên | C |

## 5. Yêu cầu phi chức năng (NFR)

### 5.1 Hiệu năng
- Trang danh sách ≤ 2s với 10.000 ứng viên (phân trang server-side, index đầy đủ).
- Lọc theo Ngành nghề & Khu vực (Tỉnh/Thành) phản hồi < 50ms nhờ Composite Index (`idx_jobs_category_location`).
- Kanban ≤ 1.5s cho 1 requisition ≤ 500 ứng viên.
- API p95 < 500ms cho CRUD thông thường.
- Upload CV ≤ 10MB, xử lý nền (AI parse CV qua BullMQ Worker < 5s).
- Inbound Webhook xử lý email bài nộp test phản hồi HTTP 200 trong < 200ms, đẩy phân tích file vào queue nền.

### 5.2 Bảo mật & tuân thủ
- Mật khẩu hash Argon2id; JWT access 15 phút + refresh 7 ngày (httpOnly cookie).
- RBAC ở cả API và UI; kiểm tra quyền theo phòng ban (row-level).
- Link làm bài test dùng Secure Token ngẫu nhiên (UUIDv4/CUID), không yêu cầu đăng nhập mật khẩu nhưng có thời hạn và gắn chặt với 1 ứng viên duy nhất.
- File CV & file bài nộp test: bucket private, URL ký hạn 5 phút, log lượt tải.
- Audit log không cho sửa/xóa, lưu ≥ 2 năm.
- Tuân thủ Nghị định 13/2023/NĐ-CP: có consent khi apply, chính sách lưu trữ, xóa dữ liệu theo yêu cầu.
- Rate limit login, chống brute force, 2FA cho Admin (phase 2).
- HTTPS bắt buộc, security headers, validate input (class-validator), chống SQL injection/XSS/CSRF.

### 5.3 Khả dụng & vận hành
- Uptime mục tiêu 99.5% giờ hành chính.
- Backup PostgreSQL hằng ngày, giữ 30 ngày, test restore hằng quý.
- Health check + log tập trung; cảnh báo lỗi 5xx.

### 5.4 Trải nghiệm
- Responsive: desktop ưu tiên, mobile dùng được cho duyệt/scorecard/làm bài test.
- Tiếng Việt mặc định, chuẩn bị i18n EN.
- Accessibility cơ bản: keyboard nav, contrast AA, label form.
- Trạng thái rỗng/đang tải/lỗi rõ ràng; thao tác nguy hiểm có xác nhận.

### 5.5 Kỹ thuật
- TypeScript strict, lint + format tự động, test unit cho logic nghiệp vụ chính, e2e cho luồng tuyển dụng.
- Migration DB có version, seed dữ liệu demo.
- CI chạy lint/test/build trước khi merge.

## 6. Quy tắc nghiệp vụ (Business Rules)

| ID | Quy tắc |
|---|---|
| BR-01 | Không thể publish tin nếu requisition chưa được duyệt. |
| BR-02 | Không thể tạo offer nếu ứng viên chưa qua vòng PV cuối (hoặc HR Manager override có ghi log). |
| BR-03 | Reject ứng viên bắt buộc chọn lý do; lý do "Khác" bắt buộc nhập mô tả. |
| BR-04 | Một ứng viên có thể ứng tuyển nhiều vị trí; mỗi cặp (ứng viên, requisition) là một Application riêng. |
| BR-05 | Email ứng viên là duy nhất trong hệ thống; SĐT chuẩn hóa theo định dạng VN. |
| BR-06 | Chỉ người phụ trách, Hiring Manager của job, HR Manager, Admin được xem CV. |
| BR-07 | Interviewer chỉ thấy ứng viên mình được phân công PV. |
| BR-08 | Khi requisition đóng/hủy, application đang active chuyển sang Talent pool hoặc Rejected theo lựa chọn của Recruiter. |
| BR-09 | Offer vượt ngưỡng lương cấu hình phải qua BOD duyệt. |
| BR-10 | Xóa mềm (soft delete) cho ứng viên, requisition, offer; dữ liệu vẫn truy vết trong audit. |
| BR-11 | Mọi thay đổi trạng thái quan trọng đều ghi Activity + Audit. |
| BR-12 | Ứng viên không được xem điểm đánh giá nội bộ. |
| BR-13 | Nếu câu hỏi sàng lọc được đánh dấu là "Knock-out" và ứng viên không đạt tiêu chí bắt buộc, hệ thống tự động gắn nhãn "Không đạt tiêu chí knock-out" để Recruiter lọc nhanh. |
| BR-14 | Thời điểm nộp bài test được ghi nhận theo: (1) thời điểm bấm Nộp trên giao diện Web; hoặc (2) thời điểm gửi thư (`Date` / `received_at` trong Email Header) khi ứng viên Reply email. |
| BR-15 | Nếu thời điểm nộp bài test ≤ deadline $\rightarrow$ trạng thái `SUBMITTED` (Đúng hạn); nếu > deadline $\rightarrow$ trạng thái `SUBMITTED_LATE` (Nộp trễ) và ghi rõ số giờ trễ hạn. |
| BR-16 | Khi hệ thống nhận được email nộp bài test hợp lệ, tác vụ nền tự động hủy/bỏ qua cảnh báo quá hạn cho bài test đó. |
| BR-17 | Ứng viên nộp hồ sơ mở (Talent Network) được lưu ở trạng thái `TALENT_POOL` và tự động gắn tags kỹ năng qua AI CV Parser. |
| BR-18 | Trả kết quả phỏng vấn 1-click: Nhấn nút "Pass" sẽ tự động gửi email chúc mừng, chuyển ứng viên sang vòng tiếp theo trong Kanban và cập nhật trạng thái "Đạt vòng PV" trên cổng tra cứu ứng viên. Nhấn nút "Deny" sẽ hiển thị popup chọn nhanh lý do loại, sau đó tự động gửi email cảm ơn & từ chối, chuyển hồ sơ sang cột Từ chối và cập nhật trạng thái tương ứng trên cổng tra cứu ứng viên. |

## 7. Tiêu chí nghiệm thu mẫu (Acceptance Criteria)

**AC-01 — Tạo và duyệt yêu cầu tuyển dụng**
- Given Recruiter đã đăng nhập và có quyền tạo requisition
- When tạo requisition với đủ trường bắt buộc và bấm "Gửi duyệt"
- Then requisition chuyển sang `PENDING_APPROVAL`, Hiring Manager nhận thông báo, và không thể sửa các trường chính cho đến khi có kết quả.

**AC-02 — Kéo ứng viên sang bước phỏng vấn**
- Given ứng viên đang ở bước `SÀNG LỌC`
- When Recruiter kéo sang `PV SƠ LOẠI`
- Then hệ thống ghi lịch sử chuyển bước và mở form đặt lịch PV (nếu bước yêu cầu lịch).

**AC-03 — Reject có lý do**
- Given ứng viên đang ở bất kỳ bước nào trước Offer
- When người dùng chọn "Từ chối"
- Then bắt buộc chọn lý do; nếu chọn "Khác" phải nhập mô tả; sau khi lưu, ứng viên rời pipeline active và email từ chối được gửi (nếu bật).

**AC-04 — Scorecard**
- Given interviewer được phân công PV
- When mở lịch PV và chấm điểm đủ tiêu chí bắt buộc
- Then lưu đánh giá, hiển thị điểm trung bình cho HR/Hiring Manager, interviewer không sửa được sau khi HR khóa.

**AC-05 — Offer**
- Given ứng viên đã PV cuối và được đánh giá pass
- When HR tạo offer và gửi duyệt
- Then offer vào `PENDING_APPROVAL`; khi duyệt xong có thể gửi; khi ứng viên accept, hệ thống tạo onboarding và đánh dấu ứng viên `HIRED`.

**AC-06 — Career site apply**
- Given khách truy cập career site
- When apply vào một tin đang mở với CV hợp lệ, trả lời đủ câu hỏi sàng lọc và tick consent
- Then tạo hồ sơ + application, gửi email xác nhận kèm mã hồ sơ, hồ sơ xuất hiện trong pipeline của Recruiter.

**AC-07 — Giao và nộp bài test (Web & Email Reply)**
- Given ứng viên đang ở bước Bài test trong pipeline
- When Recruiter chọn giao đề bài kèm deadline 23:59 ngày X
- Then ứng viên nhận được email đề bài có link web bảo mật và địa chỉ reply định danh riêng; nếu ứng viên Reply email kèm file bài nộp trước 23:59 ngày X, hệ thống tự động bóc tách file lưu vào MinIO, cập nhật trạng thái `SUBMITTED`, hủy cảnh báo quá hạn và báo cho Recruiter chấm điểm.

**AC-08 — Câu hỏi sàng lọc Knock-out**
- Given tin tuyển dụng có câu hỏi knock-out "Bạn có thể đi làm tại TP.HCM không? (Có/Không)"
- When ứng viên trả lời "Không" và hoàn tất nộp đơn
- Then hồ sơ được gắn cờ cảnh báo `KNOCK_OUT_FAILED` trong danh sách của Recruiter để lọc và loại bỏ nhanh chóng.

**AC-09 — Trả kết quả phỏng vấn 1-click (Pass / Deny)**
- Given Recruiter / HR mở chi tiết buổi phỏng vấn đã hoàn tất chấm Scorecard
- When Recruiter bấm nút "Pass"
- Then hệ thống tự động gửi email chúc mừng vượt qua vòng PV (qua Resend), đẩy ứng viên sang vòng tiếp theo trong Kanban, và cập nhật trạng thái "Đã đạt vòng [Tên vòng PV]" trên cổng tra cứu `/careers/status` của ứng viên.
- When Recruiter bấm nút "Deny" và chọn lý do từ chối
- Then hệ thống tự động gửi email cảm ơn & từ chối (qua Resend), chuyển hồ sơ sang cột Từ chối trong Kanban, và cập nhật trạng thái "Chưa phù hợp" trên cổng tra cứu của ứng viên.

## 8. Glossary

| Thuật ngữ | Nghĩa |
|---|---|
| Requisition | Yêu cầu tuyển dụng cho một vị trí |
| Job Posting | Tin tuyển dụng công khai |
| Application | Một lần ứng tuyển của ứng viên vào một vị trí |
| Pipeline | Chuỗi bước tuyển dụng của một application |
| Stage | Bước trong pipeline |
| Scorecard | Phiếu đánh giá ứng viên theo tiêu chí |
| Assessment | Bài kiểm tra / đánh giá năng lực ứng viên (Take-home / Quiz) |
| Screening Question | Câu hỏi sàng lọc ứng viên trước khi nộp đơn |
| Knock-out Question | Câu hỏi loại trực tiếp nếu ứng viên không thỏa điều kiện |
| Talent Network / Pool | Kho ứng viên tiềm năng chưa tuyển ngay hoặc gửi CV mở |
| Inbound Webhook | Cơ chế nhận và xử lý email trả lời từ ứng viên tự động |
| SLA | Thời gian xử lý tối đa cho một bước |
| Time-to-hire | Từ lúc ứng viên apply đến khi nhận việc |
| Time-to-fill | Từ lúc mở requisition đến khi tuyển đủ |
| Offer | Đề nghị làm việc gửi ứng viên |
| Onboarding | Quy trình tiếp nhận nhân viên mới |
