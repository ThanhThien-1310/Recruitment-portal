# 05 — Kiến trúc kỹ thuật

## 1. Stack

| Lớp | Lựa chọn | Lý do |
|---|---|---|
| Frontend | **Next.js 15 (App Router) + React 19 + TypeScript** | SSR cho career site/SEO, RSC giảm JS, một framework cho cả nội bộ và public |
| UI | Tailwind CSS v4 + shadcn/ui + Radix | Nhanh, accessible, dễ tùy biến thương hiệu |
| Data fetching | TanStack Query + Server Actions cho form đơn giản | Cache, optimistic update, devtools |
| Form | React Hook Form + Zod | Validate chia sẻ với backend |
| Bảng | TanStack Table | Sort/filter/column visibility |
| Kanban | dnd-kit | Kéo–thả accessible |
| Chart | Recharts | Đủ dùng, nhẹ |
| Backend | **NestJS 11 + TypeScript** | Module hóa, DI, guard/interceptor, dễ test |
| ORM | Prisma | Type-safe, migration rõ ràng |
| DB | **PostgreSQL 16** | JSONB, full-text, GIN, window function cho báo cáo |
| Cache/Queue | Redis 7 + BullMQ | Job nền: email, nhắc lịch, SLA scan, export |
| File | MinIO (S3-compatible) | Self-host, presigned URL |
| Email | **Resend (Transactional + Inbound Webhook)** + MailHog ở dev | Miễn phí 3.000 mail/tháng, webhook nhận bài test qua reply email |
| AI / LLM | **Google Gemini Flash API** | Tốc độ cao, chi phí cực thấp, parse CV và matching kỹ năng |
| Auth | JWT access + refresh rotation, httpOnly cookie | Đơn giản, an toàn, không phụ thuộc bên thứ ba |
| RBAC | CASL (ability) + guard NestJS | Khai báo quyền tập trung, dùng được ở cả FE |
| API docs | Swagger/OpenAPI | Tài liệu sống cho FE/QC |
| Test | Jest + Supertest + Playwright | Unit/Integration/E2E |
| Deploy | Docker Compose + Caddy/Nginx | Phù hợp on-prem/VPS nội bộ |
| Monorepo | npm workspaces + Turborepo | Chia sẻ type/validation giữa FE–BE |

## 2. Kiến trúc tổng thể

```mermaid
flowchart TB
    subgraph Client
        B[Browser / Mobile web]
    end
    subgraph Edge
        C[Caddy / Nginx<br/>HTTPS + reverse proxy]
    end
    subgraph App
        W[Next.js Web<br/>:3000]
        A[NestJS API<br/>:4000]
        K[Worker BullMQ]
    end
    subgraph Data
        P[(PostgreSQL 16)]
        R[(Redis 7)]
        M[(MinIO / S3)]
    end
    SMTP[SMTP / Email provider]

    B --> C
    C --> W
    C --> A
    W -- SSR fetch --> A
    A --> P
    A --> R
    A --> M
    K --> P
    K --> R
    K --> M
    K --> SMTP
    A -- enqueue --> R
```

- Web gọi API qua `NEXT_PUBLIC_API_URL`; career site dùng SSR/ISR để SEO.
- Upload CV: client xin presigned URL → upload trực tiếp MinIO → gọi API xác nhận (giảm tải API).
- Worker tách riêng process, cùng codebase API.

## 3. Cấu trúc repo

```
recruitment-portal/
├─ apps/
│  ├─ web/                        # Next.js
│  │  ├─ src/app/
│  │  │  ├─ (auth)/login, forgot-password, reset-password, accept-invite
│  │  │  ├─ (dashboard)/
│  │  │  │  ├─ layout.tsx         # sidebar + topbar
│  │  │  │  ├─ dashboard/
│  │  │  │  ├─ requisitions/[id]/
│  │  │  │  ├─ jobs/[id]/
│  │  │  │  ├─ candidates/[id]/
│  │  │  │  ├─ pipeline/
│  │  │  │  ├─ interviews/[id]/
│  │  │  │  ├─ offers/[id]/
│  │  │  │  ├─ onboarding/[id]/
│  │  │  │  ├─ reports/
│  │  │  │  └─ settings/
│  │  │  ├─ (public)/careers/[slug]/apply, status, thanks
│  │  │  └─ api/health/route.ts
│  │  ├─ src/components/          # ui/ (shadcn), layout/, shared/
│  │  ├─ src/features/            # feature-first: candidates, pipeline, ...
│  │  ├─ src/lib/                 # api client, auth, permissions, utils
│  │  └─ src/messages/            # i18n vi.json, en.json
│  └─ api/                        # NestJS
│     ├─ src/modules/
│     │  ├─ auth/                 # login, refresh, reset, invite
│     │  ├─ users/  roles/  departments/
│     │  ├─ requisitions/  approvals/
│     │  ├─ job-postings/  public-jobs/
│     │  ├─ candidates/  files/  tags/
│     │  ├─ applications/  pipeline/
│     │  ├─ interviews/  scorecards/
│     │  ├─ offers/
│     │  ├─ onboarding/
│     │  ├─ reports/
│     │  ├─ notifications/  emails/
│     │  ├─ settings/  audit/
│     │  └─ health/
│     ├─ src/common/              # guards, decorators, filters, interceptors, pagination
│     ├─ src/queues/              # processors: email, reminder, sla, export
│     ├─ prisma/schema.prisma
│     └─ test/
├─ packages/
│  ├─ shared/                     # DTO types, zod schema, enum, constants
│  └─ config/                     # eslint, tsconfig, tailwind preset
├─ infra/
│  ├─ docker-compose.yml
│  ├─ docker-compose.prod.yml
│  ├─ Caddyfile
│  └─ backup/                     # script pg_dump + upload
├─ docs/
└─ .github/workflows/ci.yml
```

**Quy ước code**
- Feature-first ở FE: mỗi feature gồm `components/`, `hooks/`, `api.ts`, `schemas.ts`, `types.ts`.
- BE: controller mỏng — service chứa nghiệp vụ — repository (Prisma) truy cập dữ liệu.
- DTO + Zod schema chia sẻ ở `packages/shared` để FE/BE cùng validate.
- Không gọi Prisma trực tiếp từ controller.

## 4. Xác thực & phân quyền

### 4.1 Luồng đăng nhập
1. `POST /auth/login` → kiểm tra Argon2id → phát access token (15 phút, httpOnly cookie `at`) + refresh token (7 ngày, httpOnly cookie `rt`, lưu hash trong DB, rotation).
2. FE gọi API kèm cookie; middleware Next.js kiểm tra session cho route nội bộ.
3. `POST /auth/refresh` xoay refresh token; phát hiện reuse → thu hồi toàn bộ phiên.
4. Đăng xuất: xóa cookie + revoke refresh token.

### 4.2 RBAC + phạm vi dữ liệu

```ts
// ví dụ ability
can('read', 'Candidate', { ownerId: user.id });           // recruiter
can('read', 'Candidate', { departmentId: user.deptId });  // hiring manager
can('manage', 'Setting');                                 // admin
can('approve', 'Offer', { salary: { lte: threshold } });   // hr manager
```

- Permission code dạng `module.action` (`candidate.read`, `offer.approve`, `setting.manage`).
- Guard `@RequirePermission('candidate.read')` + policy filter trong service (VD `where: { departmentId: { in: allowedDeptIds } }`).
- Scope phòng ban: lấy cây phòng ban của user (materialized path) để filter.
- FE nhận `permissions[]` khi login để ẩn/hiện UI — **không thay thế** kiểm tra BE.

### 4.3 Bảo mật
- Helmet, CORS whitelist, CSRF token cho cookie-based mutation hoặc dùng SameSite=Lax + kiểm tra Origin.
- Rate limit: login 5/15', public apply 3/phút/IP, API chung 300/phút/user.
- Validate toàn bộ input bằng `class-validator` + Zod; whitelist, forbid non-whitelisted.
- Không trả stack trace ở production; log có `requestId`.
- File: kiểm tra MIME + magic bytes, giới hạn 10MB, tên file lưu bằng UUID, bucket private, presigned 5 phút, log download.
- Audit log cho: đăng nhập, CRUD quan trọng, duyệt, export, tải CV, đổi quyền.
- Consent & xóa dữ liệu: lưu `consentAt`, endpoint ẩn/xóa hồ sơ theo yêu cầu (soft delete + ẩn khỏi tìm kiếm).

## 5. API (REST, prefix `/api/v1`)

### 5.1 Quy ước
- Phân trang: `?page=1&pageSize=20&sort=-createdAt` → `{ data: [], meta: { page, pageSize, total, totalPages } }`.
- Lọc: query param rõ ràng (`status=OPEN&departmentId=...&q=...&from=&to=`).
- Lỗi: `{ statusCode, code, message, details?, requestId }`.
- Idempotency: header `Idempotency-Key` cho tạo application/offer từ public.
- Version trong URL; breaking change → `/v2`.

### 5.2 Endpoint chính

| Module | Endpoint |
|---|---|
| Auth | `POST /auth/login` · `POST /auth/refresh` · `POST /auth/logout` · `POST /auth/forgot-password` · `POST /auth/reset-password` · `POST /auth/accept-invite` · `GET /auth/me` |
| Users | `GET/POST /users` · `GET/PATCH/DELETE /users/:id` · `POST /users/:id/lock` · `POST /users/:id/reset-password` |
| Roles | `GET/POST /roles` · `PATCH /roles/:id` · `PUT /roles/:id/permissions` |
| Departments | `GET /departments` (tree) · `POST/PATCH/DELETE /departments/:id` |
| Taxonomy | `GET/POST/PATCH/DELETE /categories` · `GET/POST/PATCH/DELETE /locations` |
| Requisitions | `GET/POST /requisitions` · `GET/PATCH/DELETE /requisitions/:id` · `POST /requisitions/:id/submit` · `POST /requisitions/:id/approve` · `POST /requisitions/:id/reject` · `POST /requisitions/:id/close` · `POST /requisitions/:id/hold` · `POST /requisitions/:id/duplicate` |
| Job postings | `GET/POST /job-postings` · `PATCH /job-postings/:id` · `POST /job-postings/:id/publish` · `POST /job-postings/:id/unpublish` · `GET /job-postings/:id/matched-candidates` |
| Screening Qs | `GET/POST/PATCH/DELETE /screening-questions` · `GET /job-postings/:id/questions` |
| Public jobs | `GET /public/jobs` (hỗ trợ filter category, location) · `GET /public/jobs/:slug` |
| Public apply | `POST /public/applications` · `POST /public/uploads/presign` · `GET /public/applications/status` · `POST /public/talent-network` |
| Candidates | `GET/POST /candidates` · `GET/PATCH/DELETE /candidates/:id` · `POST /candidates/:id/merge` · `POST /candidates/import` · `GET /candidates/export` · `POST /candidates/ai-parse` |
| Candidate files | `POST /candidates/:id/files/presign` · `POST /candidates/:id/files` · `DELETE /files/:id` · `GET /files/:id/download` |
| Tags | `GET/POST /tags` · `DELETE /tags/:id` |
| Applications | `GET/POST /applications` · `GET /applications/:id` · `POST /applications/:id/move` · `POST /applications/:id/reject` · `POST /applications/:id/withdraw` · `POST /applications/bulk-move` |
| Pipeline | `GET /pipeline/stages` · `POST/PATCH/DELETE /pipeline/stages/:id` · `PUT /pipeline/stages/reorder` · `GET /pipeline/board?requisitionId=` |
| Assessments | `GET/POST /assessments` · `POST /assessments/upload-doc-parse` (AI quét file PDF/Word bóc tách câu hỏi) · `GET/PATCH /assessments/:id` · `POST /assessments/:id/grade` · `GET /public/assessments/:token` · `GET /public/assessments/:token/download-original` (tải file đề gốc) · `POST /public/assessments/:token/save-draft` · `POST /public/assessments/:token/submit` (nộp bài khảo sát online kèm file bổ trợ) |
| Webhooks | `POST /webhooks/resend-inbound` (nhận email reply bài test từ ứng viên) |
| Interviews | `GET/POST /interviews` · `GET/PATCH /interviews/:id` · `POST /interviews/:id/cancel` · `POST /interviews/:id/complete` · `POST /interviews/:id/result` (Pass/Deny 1-click) · `GET /interviews/:id/ics` |
| Scorecards | `GET /interviews/:id/scorecards` · `PUT /interviews/:id/scorecards/me` · `POST /scorecards/:id/lock` · `GET /scorecard-templates` |
| Offers | `GET/POST /offers` · `GET/PATCH /offers/:id` · `POST /offers/:id/submit` · `POST /offers/:id/approve` · `POST /offers/:id/reject` · `POST /offers/:id/send` · `POST /offers/:id/respond` · `GET /offers/:id/pdf` |
| Onboarding | `GET /onboarding` · `GET /onboarding/:id` · `POST /onboarding/:id/tasks` · `PATCH /onboarding/tasks/:id` · `GET/POST /onboarding/templates` |
| Reports | `GET /reports/dashboard` · `GET /reports/funnel` · `GET /reports/time-to-hire` · `GET /reports/sources` · `GET /reports/recruiters` · `GET /reports/sla` · `POST /reports/export` |
| Notifications | `GET /notifications` · `POST /notifications/read-all` · `PATCH /notifications/:id/read` |
| Settings | `GET/PUT /settings` · `GET/POST/PATCH/DELETE /sources` · `/reject-reasons` · `/email-templates` · `/notification-rules` · `/approval-configs` |
| Audit | `GET /audit-logs` · `GET /audit-logs/export` |
| Health | `GET /health` · `GET /health/ready` |

## 6. Job nền (BullMQ)

| Queue | Job | Tần suất |
|---|---|---|
| `email` | Gửi email template qua Resend API (retry 3, backoff) | theo sự kiện |
| `cv-parsing` | Gọi Gemini Flash AI trích xuất kỹ năng, kinh nghiệm từ CV | theo sự kiện upload CV |
| `inbound-email` | Tải attachments từ webhook email reply bài test, đẩy MinIO, cập nhật status | theo sự kiện webhook |
| `reminder` | Nhắc PV trước 24h/1h, nhắc deadline bài test trước 24h, nhắc task onboarding | cron 15' |
| `sla` | Quét application quá SLA, tạo notification | cron 30' |
| `posting` | Tự đóng tin hết hạn/đủ chỉ tiêu, đóng requisition | cron 1h |
| `export` | Sinh file Excel/CSV, lưu tạm, gửi link | theo yêu cầu |
| `maintenance` | Dọn token hết hạn, file tạm, log cũ | cron ngày |

- Tất cả job idempotent; log kết quả; dead-letter queue có màn hình theo dõi (admin).
- Dev dùng `mailhog` để xem email.

## 7. Hiệu năng & dữ liệu lớn

- Phân trang server-side mọi danh sách; không load toàn bộ.
- Danh mục Ngành nghề & Khu vực (Tỉnh/Thành): Composite index `(locationId, categoryId, status)` + Cache Redis 24h (hoặc Next.js ISR), truy vấn lọc trang tuyển dụng < 50ms.
- Kanban: chỉ load application của 1 requisition; card nhẹ (select tối thiểu); lazy-load drawer.
- Full-text search: `tsvector` generated column + GIN (tên, email, kỹ năng, summary).
- Báo cáo: query tổng hợp có index; cache kết quả dashboard 60s trong Redis; báo cáo nặng chạy job export.
- Materialized view cho funnel/time-to-hire nếu dữ liệu > 100k application (refresh mỗi giờ).
- Connection pool Prisma phù hợp số instance; Redis dùng cho rate limit + cache permission.

## 8. Biến môi trường

```env
# chung
NODE_ENV=production
APP_URL=https://tuyendung.congty.vn
API_URL=https://tuyendung.congty.vn/api
TZ=Asia/Ho_Chi_Minh

# database / redis
DATABASE_URL=postgresql://portal:***@postgres:5432/recruitment
REDIS_URL=redis://redis:6379

# auth
JWT_ACCESS_SECRET=...
JWT_REFRESH_SECRET=...
ACCESS_TOKEN_TTL=15m
REFRESH_TOKEN_TTL=7d
COOKIE_DOMAIN=.congty.vn

# storage
S3_ENDPOINT=http://minio:9000
S3_BUCKET=cv-files
S3_ACCESS_KEY=...
S3_SECRET_KEY=...
S3_REGION=us-east-1
S3_FORCE_PATH_STYLE=true

# mail (Resend)
RESEND_API_KEY=re_123456789...
RESEND_INBOUND_SECRET=whsec_...
MAIL_FROM="Tuyển dụng Công ty ABC <tuyendung@congty.vn>"

# AI (Google Gemini Flash)
GEMINI_API_KEY=AIzaSy...

# tuỳ chọn
SENTRY_DSN=
NEXT_PUBLIC_API_URL=/api
```

## 9. Triển khai

```yaml
# infra/docker-compose.yml (rút gọn)
services:
  postgres:   # 16-alpine, volume pgdata, healthcheck
  redis:      # 7-alpine, appendonly
  minio:      # minio/minio, volume, console 9001
  api:        # build apps/api, migrate deploy khi start, port 4000
  worker:     # cùng image api, command node dist/worker.js
  web:        # build apps/web, port 3000
  caddy:      # reverse proxy, TLS tự động hoặc cert nội bộ
  mailhog:    # chỉ dev
```

**Quy trình release**
1. CI: lint → typecheck → unit test → build → e2e (Playwright, DB test) → build image.
2. CD: tag image → `docker compose pull` → `prisma migrate deploy` → rolling restart api/worker/web.
3. Backup trước migration; migration phải backward-compatible (thêm cột nullable, không xóa cột ngay).
4. Rollback: giữ 3 image gần nhất; migration có script down khi có thể.

**Vận hành**
- Log JSON (pino) → file/fluent-bit; log rotate.
- Healthcheck `/health` (liveness) + `/health/ready` (DB/Redis/S3).
- Backup `pg_dump` hằng ngày 02:00, giữ 30 bản, mã hóa, test restore hằng quý.
- Giám sát: uptime check, cảnh báo 5xx > 1%, email queue backlog > 100, disk > 80%.

## 10. Testing

| Loại | Công cụ | Phạm vi |
|---|---|---|
| Unit | Jest | service nghiệp vụ: state machine, tính SLA, RBAC, tính điểm scorecard |
| Integration | Jest + Supertest + Postgres test container | API + Prisma, transaction, validate |
| E2E | Playwright | 6 luồng: đăng nhập, tạo+duluyệt requisition, apply public, kanban move, đặt lịch+scorecard, offer accept |
| Contract | OpenAPI diff | FE/BE không lệch schema |
| Load (tuỳ chọn) | k6 | 100 user đồng thời, danh sách 10k ứng viên |

**Dữ liệu test:** factory + seed riêng, mỗi test rollback transaction; không dùng DB dev.

## 11. CI/CD (GitHub Actions)

```yaml
on: [push, pull_request]
jobs:
  quality:  lint, typecheck, unit
  integration: postgres service, migrate, test
  e2e: build, playwright
  docker: build & push (chỉ main/tag)
```

## 12. i18n & thời gian

- Chuỗi UI tập trung `messages/vi.json`; dùng `next-intl`; mặc định `vi`, sẵn `en`.
- Ngày giờ: `date-fns` + `date-fns-tz`, luôn lưu UTC, hiển thị `Asia/Ho_Chi_Minh`.
- Tiền: `Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' })`.
- Số điện thoại: chuẩn hóa `+84`, hiển thị `0xxx xxx xxx`.

## 13. Những điểm cần cân nhắc trước khi code

1. **Prisma enum vs bảng danh mục**: stage/nguồn/lý do dùng bảng để cấu hình runtime; trạng thái hệ thống dùng enum.
2. **Soft delete + unique email**: dùng partial unique index `WHERE deleted_at IS NULL`.
3. **SLA theo giờ làm việc**: cần bảng `working_hours` + `holidays` hoặc hard-code cấu hình trong `Setting`.
4. **Upload trực tiếp MinIO**: cần CORS bucket cho domain web; kiểm tra quyền trước khi presign.
5. **Cookie cross-subdomain**: nếu web và api khác subdomain, cấu hình `COOKIE_DOMAIN` + CORS credentials.
6. **Offer PDF**: dùng template HTML → PDF (Playwright/puppeteer) ở worker, lưu MinIO.
7. **Timezone**: mọi so sánh lịch PV dùng UTC trong DB, hiển thị local.
