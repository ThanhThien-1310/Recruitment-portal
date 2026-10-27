# TalentFlow Mobile — Flutter ATS & Career App (Đồ Án Kì IV)

> Ứng dụng di động quản lý phỏng vấn và tuyển dụng nhân sự viết bằng Flutter.  
> Tích hợp 2 chiều với Backend Spring Boot thông qua REST API.

---

## 📱 1. Các Tính Năng Đã Xây Dựng Trên Mobile

### A. Phân hệ Phỏng Vấn Viên (Interviewer / Lead)
- **Lịch phỏng vấn hôm nay (`InterviewScheduleScreen`):** Hiển thị chi tiết giờ họp, ứng viên, vị trí, hình thức trực tiếp (P.302) hoặc Online Google Meet.
- **Phiếu chấm điểm chuyên sâu (`ScorecardScreen`):**
  - Đánh giá 4 tiêu chí cốt lõi: *Kỹ thuật chuyên môn*, *Tư duy giải thuật*, *Kỹ năng giao tiếp*, *Văn hóa doanh nghiệp* (thang điểm 1 - 5 sao).
  - Tự động tính điểm trung bình (GPA).
  - Khung nhập nhận xét chi tiết sau buổi phỏng vấn.
  - **Quy tắc 1-Click Pass / Deny (Rule BR-18):** Bấm `Pass` hoặc `Deny` để gửi đánh giá và kích hoạt email tự động cho ứng viên.

### B. Phân hệ Ứng Viên (Candidate)
- **Khám phá việc làm (`JobListScreen`):**
  - Danh sách tin tuyển dụng mở, lọc nhanh theo phòng ban (IT, Sản phẩm, Nhân sự).
  - Xem chi tiết JD (Mô tả, Yêu cầu, Phúc lợi).
  - Form nộp CV nhanh trên điện thoại.
- **Tra cứu tiến trình hồ sơ (`TrackApplicationScreen`):**
  - Nhập mã hồ sơ (ví dụ `APP-2026-101`) hoặc email.
  - **Live Stepper 6 giai đoạn:** Nộp đơn ➔ Sơ loại CV ➔ Bài test ➔ Phỏng vấn ➔ Offer ➔ Tiếp nhận.

---

## 🔌 2. Kết Nối REST API Backend (Spring Boot)

Ứng dụng kết nối tới các endpoints đã xây dựng sẵn trên Backend:
- `GET /recruitment/api/jobs`: Lấy danh sách việc làm đang mở.
- `GET /recruitment/api/candidates`: Lấy danh sách ứng viên.
- `GET /recruitment/api/interviews`: Lấy lịch phỏng vấn.
- `POST /recruitment/api/candidates/{id}/evaluate`: Gửi phiếu đánh giá phỏng vấn.

*(Lưu ý: Ứng dụng đã có sẵn cơ chế **Offline Fallback Mock Data**, tự động hoạt động mượt mà ngay cả khi chưa bật máy chủ Spring Boot).*

---

## 🛠️ 3. Hướng Dẫn Chạy Ứng Dụng

1. Mở thư mục `recruitment_mobile` bằng **VS Code** hoặc **Android Studio**.
2. Chạy lệnh cài đặt thư viện:
   ```bash
   flutter pub get
   ```
3. Chạy ứng dụng trên thiết bị mô phỏng hoặc trình duyệt:
   ```bash
   flutter run
   ```
   Hoặc chọn thiết bị **Chrome (web)** / **Android Emulator** / **Windows**.
