# 03 — Bản đồ trang & nội dung từng màn hình

## 1. Sitemap

```
/                               → redirect /dashboard (nếu đã login) hoặc /login
/login
/forgot-password
/reset-password?token=
/accept-invite?token=

/dashboard                      Tổng quan
/requisitions                   Danh sách yêu cầu tuyển dụng
/requisitions/new               Tạo yêu cầu (wizard 3 bước)
/requisitions/[id]              Chi tiết yêu cầu (tabs)
/jobs                           Tin tuyển dụng
/jobs/[id]                      Chi tiết / soạn tin
/candidates                     Kho ứng viên
/candidates/new                 Thêm ứng viên
/candidates/[id]                Hồ sơ ứng viên (tabs)
/pipeline                       Kanban tuyển dụng
/pipeline?requisitionId=...
/assessments                    Danh sách bài test & đánh giá năng lực
/assessments/[id]               Chi tiết bài test, bài nộp (Web/Email) & chấm điểm
/interviews                     Lịch phỏng vấn (calendar + list)
/interviews/[id]                Chi tiết buổi PV + scorecard
/offers                         Danh sách offer
/offers/[id]                    Chi tiết offer
/onboarding                     Danh sách nhân viên mới
/onboarding/[id]                Checklist onboarding
/reports                        Báo cáo (tabs)
/reports/funnel
/reports/time-to-hire
/reports/sources
/reports/recruiters
/reports/sla
/settings                       Trang cấu hình (tabs dọc)
/settings/users
/settings/roles
/settings/departments
/settings/categories            Danh mục ngành nghề tuyển dụng
/settings/locations             Danh mục khu vực / tỉnh thành
/settings/screening-questions   Ngân hàng câu hỏi sàng lọc & đề test
/settings/pipeline
/settings/sources
/settings/reject-reasons
/settings/email-templates
/settings/notifications
/settings/approvals
/settings/company
/settings/audit-log

/careers                        Career site — danh sách việc làm (lọc theo Ngành & Khu vực)
/careers/talent-network         Cổng nộp CV mở (Talent Network / General Application)
/careers/[slug]                 Chi tiết JD (public)
/careers/[slug]/apply           Form ứng tuyển + câu hỏi sàng lọc (public)
/careers/status                 Tra cứu trạng thái hồ sơ (public)
/careers/thanks                 Cảm ơn sau khi apply (public)
/assessment/[token]             Cổng làm / nộp bài test bảo mật cho ứng viên (public)

/403, /404, /500
```

## 2. Layout chung (back-office)

```
┌──────────────────────────────────────────────────────────────────────────────┐
│ ☰  TalentHub        🔍 Tìm ứng viên, vị trí...      ＋ Tạo mới   🔔 3  👤 ▾ │  ← Topbar 64px
├───────────────┬──────────────────────────────────────────────────────────────┤
│ ▸ Tổng quan   │  Breadcrumb: Trang chủ / Ứng viên                            │
│ ▸ Yêu cầu     │  ┌────────────────────────────────────────────────────────┐  │
│ ▸ Tin đăng    │  │ Tiêu đề trang                            [Action chính]│  │
│ ▸ Ứng viên    │  └────────────────────────────────────────────────────────┘  │
│ ▸ Pipeline    │  ┌ Bộ lọc: [Trạng thái ▾] [Phòng ban ▾] [Nguồn ▾] [🔍] ┐  │
│ ▸ Phỏng vấn   │  └────────────────────────────────────────────────────────┘  │
│ ▸ Offer       │  ┌────────────────────────────────────────────────────────┐  │
│ ▸ Onboarding  │  │ Bảng / Kanban / Nội dung                                │  │
│ ▸ Báo cáo     │  │                                                         │  │
│ ───────────── │  │                                                         │  │
│ ▸ Cài đặt     │  └────────────────────────────────────────────────────────┘  │
│               │  Phân trang: ‹ 1 2 3 ›    20 / trang                          │
└───────────────┴──────────────────────────────────────────────────────────────┘
   Sidebar 260px (thu gọn 72px)
```

**Thành phần toàn cục**
- **Sidebar**: nhóm menu theo nghiệp vụ, badge số việc cần xử lý (VD: "Offer 2"), thu gọn được, nhớ trạng thái.
- **Topbar**: global search (Cmd/Ctrl+K), quick create (menu: ứng viên, yêu cầu, lịch PV), chuông thông báo có dropdown, avatar menu (hồ sơ, đổi mật khẩu, đăng xuất).
- **Breadcrumb** + tiêu đề + mô tả ngắn + action chính.
- **Toast** góc phải trên cho kết quả thao tác; **Confirm modal** cho hành động nguy hiểm.
- **Empty state** có minh họa + CTA; **Skeleton** khi tải; **Error state** có nút thử lại.
- **Permission**: ẩn action nếu không có quyền; API vẫn chặn.

---

## 3. Nhóm trang xác thực

### 3.1 `/login`
- **Mục đích:** đăng nhập nội bộ.
- **Nội dung:** logo + tên portal; form email + mật khẩu; "Ghi nhớ đăng nhập"; "Quên mật khẩu?"; nút Đăng nhập; footer phiên bản.
- **Trạng thái:** sai mật khẩu (không tiết lộ email tồn tại), tài khoản bị khóa, quá số lần thử.
- **Bảo mật:** rate limit 5 lần/15 phút/IP, log đăng nhập.

### 3.2 `/forgot-password`, `/reset-password`, `/accept-invite`
- Gửi email reset (hạn 30 phút), đặt mật khẩu mới có kiểm tra độ mạnh.
- Accept invite: đặt mật khẩu + xác nhận tên, kích hoạt tài khoản.

---

## 4. `/dashboard` — Tổng quan

**Quyền:** Admin, HR Manager, Recruiter (dữ liệu của mình), Hiring Manager (phòng mình), BOD.
**Mục đích:** trả lời 3 câu: đang tuyển gì? tắc ở đâu? hôm nay cần làm gì?

```
┌────────────────────────────────────────────────────────────────────────────┐
│ Xin chào, Lan 👋   Hôm nay 26/10/2026            [Khoảng thời gian: 30 ngày ▾]│
├──────────────┬──────────────┬──────────────┬──────────────┬────────────────┤
│ Vị trí mở    │ Ứng viên     │ PV tuần này  │ Offer chờ    │ Time-to-hire   │
│   12  ▲2     │  148  ▲18    │   9          │ duyệt  3     │ 24 ngày  ▼3    │
├──────────────┴──────────────┴───────┬──────┴──────────────┴────────────────┤
│ Funnel tuyển dụng (bar ngang)       │ Việc cần xử lý hôm nay               │
│ Ứng tuyển      320  ████████████    │ ● 5 hồ sơ quá SLA sàng lọc           │
│ Sàng lọc       180  ███████         │ ● 3 offer chờ duyệt                  │
│ PV sơ loại      92  ████            │ ● 2 lịch PV chưa có scorecard        │
│ PV chuyên môn   41  ██              │ ● Họp tuyển dụng 14:00               │
│ PV cuối         18  █               │                                      │
│ Offer           11  █               │ [Xem tất cả →]                       │
│ Nhận việc        8  █               │                                      │
├─────────────────────────────────────┼──────────────────────────────────────┤
│ Pipeline theo phòng ban (stacked)   │ Lịch phỏng vấn sắp tới               │
│ IT 42 · Sales 28 · Marketing 15 ... │ 10:00 Nguyễn A — Backend — Vòng 2    │
│                                     │ 14:00 Trần B — Sales — Vòng cuối     │
├─────────────────────────────────────┼──────────────────────────────────────┤
│ Hiệu quả nguồn (donut)              │ Hoạt động gần đây                    │
│ Career site / TopCV / Referral ...  │ Lan chuyển A sang PV cuối · 10:12    │
│                                     │ Minh tạo offer cho B · 09:40         │
└─────────────────────────────────────┴──────────────────────────────────────┘
```

**Chi tiết**
- 5 KPI card bấm được → drill-down sang danh sách đã lọc.
- Bộ lọc thời gian: 7/30/90 ngày, quý, tùy chọn.
- Widget theo vai trò: Recruiter thấy việc của mình; Hiring Manager thấy phòng mình; BOD thấy toàn công ty + chi phí.
- Nếu chưa có dữ liệu: empty state hướng dẫn tạo requisition đầu tiên.

---

## 5. Nhóm Yêu cầu tuyển dụng

### 5.1 `/requisitions` — Danh sách

**Nội dung**
- Tiêu đề "Yêu cầu tuyển dụng" + nút **＋ Tạo yêu cầu**.
- Tabs trạng thái: Tất cả · Nháp · Chờ duyệt · Đang tuyển · Tạm dừng · Đã đóng (kèm số lượng).
- Bộ lọc: phòng ban, độ ưu tiên, người phụ trách, khoảng ngày, tìm kiếm.
- Bảng:

| Cột | Nội dung |
|---|---|
| Mã | RQ-2026-001 (link) |
| Vị trí | Tên + loại hợp đồng |
| Phòng ban | Tên phòng |
| Chỉ tiêu | `3 / 5` + progress bar |
| Ưu tiên | Badge Cao/Trung bình/Thấp |
| Trạng thái | Badge màu |
| Người phụ trách | Avatar + tên |
| Hạn chót | Ngày + cảnh báo nếu trễ |
| Cập nhật | Thời gian tương đối |

- Sắp xếp mọi cột; phân trang 20/50/100; chọn nhiều dòng → action hàng loạt (đóng, đổi người phụ trách, export).
- Hành động mỗi dòng: Xem · Sửa · Nhân bản · Đóng.

### 5.2 `/requisitions/new` — Wizard 3 bước

```
Bước 1/3  Thông tin vị trí      ●──────○──────○
──────────────────────────────────────────────
Tên vị trí *            [ Backend Developer                ]
Phòng ban *             [ IT ▾ ]     Người phụ trách * [ Lan ▾ ]
Số lượng *              [ 2 ]        Ưu tiên [ Cao ▾ ]
Loại hợp đồng *         [ Toàn thời gian ▾ ]
Hình thức làm việc *    [ Hybrid ▾ ]  Địa điểm [ Hà Nội ]
Lương từ *  [ 20.000.000 ]  đến * [ 30.000.000 ]  ☐ Hiển thị lương trên tin
Lý do tuyển *           [ Mở rộng team ... ]
Hạn chót *              [ 30/11/2026 ]
                                    [Hủy] [Tiếp tục →]
```

- **Bước 1 — Thông tin vị trí:** các trường trên, validate range lương, hạn chót ≥ hôm nay.
- **Bước 2 — Mô tả công việc & yêu cầu:** JD rich text (mô tả, trách nhiệm, yêu cầu bắt buộc, điểm cộng, phúc lợi), kỹ năng (tag), câu hỏi sàng lọc tùy chọn.
- **Bước 3 — Phê duyệt & xem lại:** chọn luồng duyệt (mặc định theo cấu hình), người duyệt, ghi chú; preview tổng quan; nút **Lưu nháp** hoặc **Gửi duyệt**.
- Lưu nháp tự động mỗi 30s; rời trang có cảnh báo.

### 5.3 `/requisitions/[id]` — Chi tiết

**Header:** mã + tên vị trí + badge trạng thái + priority; action: Sửa (nếu được phép), Gửi duyệt, Tạo tin đăng, Tạm dừng, Đóng, Nhân bản; menu "…" (xóa mềm, export).

**Tabs**
1. **Tổng quan** — thông tin vị trí, lương, hạn chót, mô tả, kỹ năng; tiến độ `2/2`; thống kê nhanh: tổng ứng viên, đang ở từng bước, số ngày mở; người phụ trách; timeline duyệt.
2. **Ứng viên** — bảng ứng viên của requisition này (tên, bước hiện tại, nguồn, ngày apply, người phụ trách, action), nút "Thêm ứng viên".
3. **Tin đăng** — trạng thái publish, link career site, lượt xem/apply, nút sửa/publish/unpublish.
4. **Lịch sử duyệt** — từng cấp: người duyệt, thời điểm, quyết định, ý kiến.
5. **Hoạt động** — feed audit của requisition.

**Box duyệt (nếu user là người duyệt):** hiển thị nổi bật trên tab Tổng quan — thông tin tóm tắt, ô ý kiến, nút **Đồng ý** / **Từ chối**.

---

## 6. Nhóm Tin tuyển dụng

### 6.1 `/jobs` — Danh sách
- Tabs: Đang đăng · Nháp · Đã đóng.
- Bảng: Tiêu đề, Vị trí liên kết, Kênh, Lượt xem, Số apply, Ngày đăng, Hết hạn, Trạng thái.
- Action: Sửa, Publish/Unpublish, Xem trên career site, Nhân bản link.

### 6.2 `/jobs/[id]` — Soạn & publish
- **Cột trái:** form JD (tiêu đề, slug, mô tả, yêu cầu, phúc lợi, địa điểm, loại hình, mức lương, hạn chót, ảnh cover).
- **Cột phải:** preview card như trên career site (cập nhật realtime); panel "Kênh đăng": Career site (bật/tắt), các kênh ngoài (ghi nhận thủ công link); panel SEO (meta title/description).
- **Action:** Lưu nháp · Preview · Publish.
- **Validate:** chỉ publish khi requisition đã duyệt và tin có đủ trường bắt buộc.

---

## 7. Nhóm Ứng viên

### 7.1 `/candidates` — Kho ứng viên

```
Ứng viên (1.248)                                    [Import] [＋ Thêm ứng viên]
[🔍 Tên, email, SĐT, kỹ năng...]  [Trạng thái ▾] [Nguồn ▾] [Vị trí ▾]
[Phòng ban ▾] [Người phụ trách ▾] [Tag ▾] [+ Lọc nâng cao]   Lưu bộ lọc ▾
☐ | Ứng viên            | Vị trí ứng tuyển   | Bước        | Nguồn     | Phụ trách | Cập nhật
☐ | (A) Nguyễn Văn A    | Backend Developer  | PV cuối ●   | TopCV     | Lan       | 2 giờ trước
☐ | (T) Trần Thị B      | Sales Executive    | Sàng lọc ●  | Referral  | Minh      | 1 ngày trước
```

- Cột tùy chọn (column picker), chế độ xem bảng/card.
- Chọn nhiều → Gửi email, Gắn tag, Chuyển vào talent pool, Export, Xóa mềm.
- Chip filter đang áp dụng, nút "Xóa tất cả".
- Bộ lọc nâng cao: khoảng kinh nghiệm, học vấn, kỹ năng, ngày apply, điểm đánh giá.
- Saved views dùng chung/theo user.

### 7.2 `/candidates/new`
- 2 cách: **Upload CV** (kéo thả PDF/DOCX, parse gợi ý — phase sau) hoặc **Nhập tay**.
- Trường: họ tên*, email*, SĐT*, năm sinh, giới tính, địa chỉ, học vấn, kinh nghiệm, kỹ năng (tag), nguồn*, vị trí ứng tuyển (chọn requisition), người phụ trách, ghi chú, consent.
- Cảnh báo trùng: "Email này đã tồn tại — mở hồ sơ cũ?".

### 7.3 `/candidates/[id]` — Hồ sơ ứng viên

```
┌ (Avatar) Nguyễn Văn A                    [Từ chối] [Chuyển bước ▾] [Gửi email] │
│ Backend Developer · RQ-2026-001 · PV cuối · Nguồn: TopCV · Phụ trách: Lan     │
├──────────────────────────────┬────────────────────────────────────────────────┤
│ THÔNG TIN                    │ Tabs: Tổng quan | CV & tệp | Đánh giá |        │
│ Email / SĐT / Địa chỉ        │       Lịch PV | Lịch sử | Ghi chú              │
│ Kinh nghiệm / Học vấn        │                                                │
│ Kỹ năng (tag)                │  Nội dung theo tab                             │
│ Nguồn / Ngày apply           │                                                │
│ CV chính (xem/tải)           │                                                │
│ Tag                          │                                                │
└──────────────────────────────┴────────────────────────────────────────────────┘
```

**Tabs**
1. **Tổng quan:** tóm tắt, thông tin liên hệ (có nút copy), vị trí đang ứng tuyển, điểm TB scorecard, timeline nhanh, ghi chú nội bộ (rich text, @mention).
2. **CV & tệp:** danh sách file (tên, loại, dung lượng, người tải, ngày), upload mới, đặt CV chính, tải xuống (log).
3. **Đánh giá:** bảng scorecard theo vòng PV: interviewer, tiêu chí, điểm, nhận xét; điểm trung bình; kết luận đề xuất (Pass/Fail/Cân nhắc). Interviewer chỉ thấy/chấm phiếu của mình.
4. **Lịch PV:** danh sách buổi PV (vòng, thời gian, người PV, trạng thái), nút tạo lịch.
5. **Lịch sử:** toàn bộ chuyển bước + thay đổi quan trọng, dạng timeline.
6. **Ghi chú:** ghi chú nội bộ có thể ghim; không hiển thị cho ứng viên.

**Action header:** Chuyển bước (dropdown các stage hợp lệ) · Từ chối (modal lý do) · Gửi email (chọn template) · Thêm vào talent pool · In/Export PDF hồ sơ.

**Modal chuyển bước**
```
Chuyển ứng viên sang bước *  [PV chuyên môn ▾]
Ghi chú                     [........................]
☐ Tạo lịch phỏng vấn ngay
                            [Hủy] [Xác nhận]
```

**Modal từ chối**
```
Lý do từ chối *   [ Chưa đủ kinh nghiệm ▾ ]
Mô tả thêm        [................................]
☑ Gửi email thông báo cho ứng viên
☐ Đưa vào talent pool
                            [Hủy] [Từ chối]
```

---

## 8. `/pipeline` — Kanban tuyển dụng

**Mục đích:** nơi làm việc chính hằng ngày của Recruiter.

```
[Vị trí: Backend Developer (RQ-2026-001) ▾]  [Người phụ trách ▾] [Nguồn ▾]  [Kanban|Danh sách]
┌─────────────┬─────────────┬─────────────┬─────────────┬─────────────┬──────────────┐
│ ỨNG TUYỂN 12│ SÀNG LỌC 8  │ PV SƠ LOẠI 5│ PV CHUYÊN 3 │ PV CUỐI 2   │ OFFER 1      │
│ SLA 1 ngày  │ SLA 2 ngày  │ ● 1 quá hạn │             │             │              │
├─────────────┼─────────────┼─────────────┼─────────────┼─────────────┼──────────────┤
│┌───────────┐│┌───────────┐│┌───────────┐│             │             │              │
││Nguyễn A   │││Trần B     │││Lê C       ││             │             │              │
││TopCV · 2d │││Referral·5d│││TopCV · 8d ││             │             │              │
││👤 Lan     │││👤 Minh    │││👤 Lan  🔴 ││             │             │              │
│└───────────┘│└───────────┘│└───────────┘│             │             │              │
│┌───────────┐│             │             │             │             │              │
││Phạm D     ││             │             │             │             │              │
│└───────────┘│             │             │             │             │              │
└─────────────┴─────────────┴─────────────┴─────────────┴─────────────┴──────────────┘
```

**Tương tác**
- Kéo–thả card giữa các cột; nếu stage đích cần lịch PV → mở modal tạo lịch; nếu là Từ chối → modal lý do (không cho drop nếu hủy).
- Click card → drawer hồ sơ nhanh (không rời trang): thông tin, CV, nút chuyển bước, ghi chú.
- Cột có số lượng, SLA, cảnh báo đỏ; cuộn ngang; cột "Từ chối" và "Talent pool" thu gọn ở cuối (có thể ẩn).
- Chọn nhiều card (Ctrl/Cmd + click) → bulk move/reject.
- Bộ lọc áp dụng cho toàn board; nút "Tải lại"; realtime cập nhật (polling 30s hoặc WebSocket phase 2).

**Card hiển thị:** tên, vị trí (nếu xem nhiều job), nguồn, số ngày ở bước, avatar người phụ trách, badge điểm TB (nếu có), icon có CV/ghi chú.

**Chế độ Danh sách:** bảng phẳng với cột stage, filter, sắp xếp; phù hợp xử lý hàng loạt.

---

## 9. Nhóm Phỏng vấn

### 9.1 `/interviews` — Lịch

- **View:** Calendar (Ngày/Tuần/Tháng) | Danh sách.
- Bộ lọc: người PV, vòng, trạng thái, vị trí, phòng ban.
- Sự kiện trên lịch: giờ, tên ứng viên, vòng, người PV; màu theo trạng thái (đã lên lịch/xác nhận/hoàn thành/huỷ).
- Click sự kiện → popover tóm tắt + link chi tiết.
- Nút **＋ Đặt lịch PV**.
- Cảnh báo trùng lịch interviewer hiển thị ngay khi chọn giờ.

**Modal đặt lịch**
```
Ứng viên *        [Nguyễn Văn A ▾]
Vị trí            Backend Developer (auto)
Vòng *            [PV chuyên môn ▾]
Ngày *  [26/10/2026]  Giờ * [14:00]  Thời lượng * [60 phút ▾]
Hình thức *       [Online ▾]   Link/Địa điểm * [https://meet...]
Người phỏng vấn * [Lan ▾][Minh ▾][+ Thêm]
Tiêu chí đánh giá [Technical Interview ▾]
Ghi chú gửi ứng viên [................................]
☑ Gửi email mời ngay
                                       [Hủy] [Tạo lịch]
```

### 9.2 `/interviews/[id]` — Chi tiết & scorecard

- **Header:** ứng viên, vòng, thời gian, trạng thái; action: Đổi lịch, Huỷ, Đánh dấu hoàn thành.
- **Top Action Bar (Trả kết quả 1-click cho HR):**
  - Nút **[Pass (Đạt)]** (xanh): bấm là hệ thống tự động gửi email chúc mừng `INTERVIEW_PASSED` cho ứng viên, chuyển pipeline sang bước kế tiếp / Offer và cập nhật trạng thái "Đạt" trên Cổng tra cứu `/careers/status`.
  - Nút **[Deny (Từ chối)]** (đỏ): bấm mở popup chọn nhanh lý do từ chối (1 giây), hệ thống tự động gửi email cảm ơn & từ chối `INTERVIEW_DENIED`, chuyển hồ sơ sang cột Từ chối và cập nhật trạng thái tương ứng trên Cổng tra cứu ứng viên.
- **Thông tin:** người tham gia (trạng thái xác nhận), địa điểm/link, tài liệu đính kèm, ghi chú.
- **Scorecard của tôi** (nếu là interviewer): các tiêu chí, thang điểm 1–5, nhận xét từng tiêu chí, kết luận Pass/Fail/Cân nhắc, nút Lưu nháp/Gửi.
- **Tổng hợp** (HR/Hiring Manager): bảng điểm từng interviewer, điểm TB có trọng số, biểu đồ radar, nhận xét tổng hợp.
- Sau khi HR khóa đánh giá → không sửa được (trừ Admin, có log).

---

## 10. Nhóm Offer

### 10.1 `/offers` — Danh sách
- Tabs: Nháp · Chờ duyệt · Đã duyệt · Đã gửi · Chấp nhận · Từ chối/Hết hạn.
- Bảng: Ứng viên, Vị trí, Phòng ban, Lương đề xuất, Ngày bắt đầu, Người duyệt hiện tại, Trạng thái, Hạn phản hồi.
- Action: Xem, Duyệt (nếu là người duyệt), Gửi, Tải PDF.

### 10.2 `/offers/[id]` — Chi tiết
- **Cột trái:** thông tin offer (chức danh, phòng ban, quản lý trực tiếp, lương gross, phụ cấp, thưởng, loại hợp đồng, ngày bắt đầu, thời gian thử việc, hạn phản hồi, ghi chú), so sánh với range của requisition (thanh min–max + điểm đề xuất).
- **Cột phải:** timeline duyệt (từng cấp, trạng thái, ý kiến, thời gian); box hành động theo vai trò; preview offer letter.
- **Modal duyệt:** thông tin tóm tắt + ô ý kiến + Đồng ý/Từ chối.
- **Khi gửi:** chọn template, đính kèm PDF, đặt hạn phản hồi; trạng thái `SENT`.
- **Ứng viên phản hồi:** HR ghi nhận Accept/Decline (hoặc ứng viên bấm link trong email — phase 2), ghi lý do nếu decline.

---

## 11. Nhóm Onboarding

### 11.1 `/onboarding` — Danh sách nhân viên mới
- Bảng: Họ tên, Vị trí, Phòng ban, Ngày bắt đầu, Quản lý trực tiếp, Tiến độ (progress + %), Task quá hạn, Trạng thái.
- Lọc theo phòng ban, tháng bắt đầu, trạng thái.

### 11.2 `/onboarding/[id]` — Checklist
- Header: nhân viên, ngày bắt đầu, đếm ngược, tiến độ tổng.
- Nhóm task: **Giấy tờ & hợp đồng**, **Tài khoản & thiết bị**, **Đào tạo & hội nhập**, **Bàn giao công việc**.
- Mỗi task: tên, owner, deadline, trạng thái (Chưa làm/Đang làm/Xong), file đính kèm, ghi chú.
- Nút thêm task, đánh dấu hoàn tất; email nhắc khi đến hạn.

---

## 12. `/reports` — Báo cáo

**Tabs:** Funnel · Time-to-hire · Nguồn · Recruiter · SLA · Phòng ban.
**Bộ lọc chung:** khoảng thời gian (mặc định 90 ngày), phòng ban, vị trí, nguồn.
**Mỗi báo cáo gồm:** 3–4 KPI card, biểu đồ chính, bảng chi tiết, nút Export Excel/CSV, nút "Ghim vào dashboard".

| Báo cáo | Nội dung |
|---|---|
| Funnel | Số lượng & tỷ lệ chuyển đổi từng bước; so sánh kỳ trước; lọc theo job/phòng ban |
| Time-to-hire | TB, median, p90 theo tháng/phòng ban/vị trí; thời gian từng bước |
| Nguồn | CV theo nguồn, tỷ lệ pass sàng lọc, tỷ lệ hired, chi phí ước tính/nguồn |
| Recruiter | Số job, số CV xử lý, số hire, time-to-hire TB, tỷ lệ phản hồi đúng SLA |
| SLA | Tỷ lệ đúng hạn theo stage, danh sách hồ sơ vi phạm, xu hướng |
| Phòng ban | Nhu cầu vs đáp ứng, số vị trí mở, thời gian tuyển, chi phí |

**Lưu ý:** mọi con số phải định nghĩa rõ trong tooltip (VD: time-to-hire tính từ apply → accept offer).

---

## 13. `/settings` — Cài đặt

Layout 2 cột: menu dọc trái + nội dung phải. Chỉ Admin (HR Manager xem).

| Trang | Nội dung chính |
|---|---|
| **Người dùng** | Bảng user (tên, email, vai trò, phòng ban, trạng thái, đăng nhập cuối); mời user, sửa, khóa, reset mật khẩu, gán nhiều vai trò |
| **Vai trò & quyền** | Danh sách role; ma trận checkbox quyền theo module/action; role hệ thống không xóa được |
| **Phòng ban** | Cây phòng ban (thêm/sửa/xóa, trưởng phòng, phòng cha) |
| **Pipeline** | Danh sách stage kéo–thả sắp xếp; mỗi stage: tên, màu, loại, SLA, yêu cầu lịch PV, tiêu chí bắt buộc, bật/tắt |
| **Nguồn ứng viên** | CRUD nguồn, chi phí ước tính/ứng viên, trạng thái |
| **Lý do từ chối** | CRUD lý do theo nhóm (kinh nghiệm, kỹ năng, lương, văn hóa, ứng viên rút) |
| **Mẫu email** | Danh sách template theo sự kiện; editor rich text + danh sách biến; preview + gửi thử |
| **Thông báo** | Bảng sự kiện × kênh (in-app/email), bật/tắt, người nhận mặc định |
| **Ngưỡng phê duyệt** | Ngưỡng lương cần BOD duyệt; luồng duyệt requisition/offer; người duyệt theo phòng ban |
| **Công ty** | Tên, logo, giới thiệu, phúc lợi, ảnh — dùng cho career site; thông tin liên hệ |
| **Audit log** | Bảng log (thời gian, user, hành động, đối tượng, IP); filter + export; chỉ đọc |

---

## 14. Career site (public)

### 14.1 `/careers` — Danh sách việc làm
```
┌──────────────────────────────────────────────────────────────────────┐
│  [Logo] Công ty ABC                        [Về chúng tôi] [Việc làm] │
├──────────────────────────────────────────────────────────────────────┤
│              Gia nhập đội ngũ của chúng tôi                          │
│         [🔍 Tìm vị trí...]  [Phòng ban ▾] [Địa điểm ▾] [Loại hình ▾] │
├──────────────────────────────────────────────────────────────────────┤
│ ┌──────────────────────────────────────────────────────────────────┐ │
│ │ Backend Developer            IT · Hà Nội · Hybrid · 20–30tr      │ │
│ │ Ứng tuyển trước 30/11/2026                    [Xem chi tiết →]   │ │
│ └──────────────────────────────────────────────────────────────────┘ │
│ ┌──────────────────────────────────────────────────────────────────┐ │
│ │ Sales Executive        Sales · TP.HCM · Toàn thời gian           │ │
│ └──────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────┘
```
- Chỉ hiển thị tin `PUBLISHED` và requisition đang mở.
- Card: tiêu đề, phòng ban, địa điểm, hình thức, lương (nếu bật), hạn chót.
- Sắp xếp: mới nhất / hết hạn gần; phân trang hoặc "Xem thêm".
- SEO: metadata động, Open Graph, sitemap.xml.

### 14.2 `/careers/[slug]` — Chi tiết JD
- Tiêu đề, thông tin nhanh (phòng ban, địa điểm, loại hình, lương, hạn), nội dung JD (rich text), nút **Ứng tuyển ngay** (sticky mobile), nút chia sẻ.
- Khối "Quy trình tuyển dụng" (các bước), "Phúc lợi", "Về công ty".

### 14.3 `/careers/[slug]/apply` — Form ứng tuyển
```
Họ và tên *        [................]
Email *            [................]
Số điện thoại *    [................]
CV *               [ Kéo thả PDF/DOCX hoặc Chọn file ]  (≤10MB)
Thư giới thiệu     [................]
Nguồn biết tin     [ TopCV ▾ ]
☑ Tôi đồng ý cho phép công ty xử lý dữ liệu cá nhân theo chính sách
                                       [Gửi hồ sơ]
```
- Validate inline; chống spam (honeypot + rate limit); upload có progress.
- Thành công → `/careers/thanks` hiển thị mã hồ sơ + hướng dẫn tra cứu.

### 14.4 `/careers/status` — Tra cứu
- Nhập email + mã hồ sơ → hiển thị trạng thái hiện tại (đã nhận, đang xem xét, mời PV, offer, kết thúc) + lịch PV sắp tới.
- Không tiết lộ điểm đánh giá nội bộ hay thông tin người khác.

---

## 15. Design system (đề xuất)

| Yếu tố | Giá trị |
|---|---|
| Font | Inter (UI) + system fallback; tiêu đề 600, body 400 |
| Màu chính | Indigo `#4F46E5` (action), hover `#4338CA` |
| Màu trạng thái | Xanh lá `#16A34A` (thành công/hired), Vàng `#F59E0B` (chờ), Đỏ `#DC2626` (từ chối/quá hạn), Xám `#6B7280` (nháp/đóng), Xanh dương `#0EA5E9` (đang PV) |
| Nền | `#F8FAFC`, card trắng, viền `#E2E8F0` |
| Bo góc | 8px (control), 12px (card) |
| Khoảng cách | thang 4px (4/8/12/16/24/32/48) |
| Component | Button, Input, Select, Combobox, DatePicker, RichText, Tabs, Badge, Avatar, Table (sort/filter/sticky header), Modal/Drawer, Toast, Tooltip, Pagination, EmptyState, Skeleton, Progress, FileUpload, Timeline, KanbanCard |
| Biểu đồ | Recharts (bar, line, donut, funnel custom) |
| Icon | Lucide |
| Dark mode | Chưa cần ở MVP, thiết kế token sẵn sàng |

**Quy ước UX**
- Mọi form: validate on blur, lỗi dưới field, nút submit disable khi đang gửi.
- Bảng: sticky header, sort, chọn dòng, phân trang server-side, nhớ bộ lọc trên URL.
- Hành động phá hủy: modal xác nhận + nút đỏ, không dùng confirm của trình duyệt.
- Thời gian hiển thị tương đối ("2 giờ trước") + tooltip tuyệt đối.
- Mobile: bảng chuyển card; Kanban cuộn ngang; action chính sticky bottom.

## 16. Trạng thái màn hình cần xử lý

| Trạng thái | Cách hiển thị |
|---|---|
| Đang tải | Skeleton theo layout, không spinner toàn trang |
| Rỗng (chưa có dữ liệu) | Minh họa + mô tả + CTA tạo mới |
| Rỗng do lọc | "Không tìm thấy kết quả" + nút xóa lọc |
| Lỗi mạng | Thông báo + nút thử lại, giữ dữ liệu đã nhập |
| Không có quyền | /403 với gợi ý liên hệ Admin |
| Bản ghi không tồn tại/đã xóa | /404 |
| Đang gửi form | Nút loading, chặn double submit |
| Xung đột dữ liệu | "Bản ghi đã được người khác cập nhật" + tải lại |
