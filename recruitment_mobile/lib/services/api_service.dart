import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/job_posting.dart';
import '../models/candidate.dart';
import '../models/interview_schedule.dart';

class ApiService {
  // 10.0.2.2 cho Android Emulator, localhost cho Chrome/Windows/iOS
  static const String baseUrl = 'http://10.0.2.2:8080';

  // Lấy danh sách việc làm
  static Future<List<JobPosting>> getJobs() async {
    try {
      final response = await http
          .get(Uri.parse('$baseUrl/recruitment/api/jobs'))
          .timeout(const Duration(seconds: 3));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        return data.map((json) => JobPosting.fromJson(json)).toList();
      }
    } catch (_) {
      // Fallback offline mock data nếu chưa kết nối tới backend
    }

    return _mockJobs;
  }

  // Lấy danh sách ứng viên
  static Future<List<Candidate>> getCandidates() async {
    try {
      final response = await http
          .get(Uri.parse('$baseUrl/recruitment/api/candidates'))
          .timeout(const Duration(seconds: 3));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        return data.map((json) => Candidate.fromJson(json)).toList();
      }
    } catch (_) {}

    return _mockCandidates;
  }

  // Lấy danh sách lịch phỏng vấn
  static Future<List<InterviewSchedule>> getInterviews() async {
    try {
      final response = await http
          .get(Uri.parse('$baseUrl/recruitment/api/interviews'))
          .timeout(const Duration(seconds: 3));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        return data.map((json) => InterviewSchedule.fromJson(json)).toList();
      }
    } catch (_) {}

    return _mockInterviews;
  }

  // Gửi chấm điểm phỏng vấn
  static Future<bool> submitEvaluation({
    required int interviewId,
    required double techScore,
    required double problemScore,
    required double commScore,
    required double cultureScore,
    required String notes,
    required String decision,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/recruitment/api/candidates/$interviewId/evaluate'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'technicalScore': techScore,
          'problemSolvingScore': problemScore,
          'communicationScore': commScore,
          'culturalFitScore': cultureScore,
          'notes': notes,
          'decision': decision,
        }),
      ).timeout(const Duration(seconds: 3));

      return response.statusCode == 200;
  // Lấy danh sách phê duyệt (Offer & Requisition)
  static Future<List<Map<String, dynamic>>> getApprovals() async {
    try {
      final response = await http
          .get(Uri.parse('$baseUrl/recruitment/api/approvals'))
          .timeout(const Duration(seconds: 3));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        return data.cast<Map<String, dynamic>>();
      }
    } catch (_) {}

    return _mockApprovals;
  }

  // Gửi thao tác phê duyệt
  static Future<bool> submitApprovalAction(int id, String action) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/recruitment/api/approvals/$id/action'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'action': action}),
      ).timeout(const Duration(seconds: 3));

      return response.statusCode == 200;
    } catch (_) {
      return true; // Simulate offline success
    }
  }

  // Lấy tóm tắt CV nhanh
  static Future<Map<String, dynamic>?> getCandidateCvSummary(int id) async {
    try {
      final response = await http
          .get(Uri.parse('$baseUrl/recruitment/api/candidates/$id/cv-summary'))
          .timeout(const Duration(seconds: 3));

      if (response.statusCode == 200) {
        return jsonDecode(utf8.decode(response.bodyBytes));
      }
    } catch (_) {}

    return null;
  }

  // Dữ liệu mẫu dự phòng
  static final List<JobPosting> _mockJobs = [
    JobPosting(
      id: 1,
      title: 'Senior Java Backend Engineer',
      department: 'Phòng Kỹ thuật & Công nghệ',
      location: 'Hà Nội / Hybrid',
      type: 'Toàn thời gian',
      salary: '25 - 35 Triệu VNĐ',
      quantity: 2,
      deadline: '15/11/2026',
      status: 'Đang tuyển',
      applicantCount: 28,
      description: 'Phát triển kiến trúc Microservices hiệu năng cao bằng Spring Boot, Docker, Redis, Kafka.',
      requirements: 'Tối thiểu 3 năm kinh nghiệm với Java Core, Spring Boot, MySQL/PostgreSQL tối ưu Index.',
      benefits: 'Lương tháng 13 + thưởng KPIs, cấp MacBook Pro M3, bảo hiểm sức khỏe VIP.',
    ),
    JobPosting(
      id: 2,
      title: 'Senior Flutter Mobile Developer',
      department: 'Phòng Kỹ thuật & Công nghệ',
      location: 'Hà Nội / Remote',
      type: 'Toàn thời gian',
      salary: '18 - 28 Triệu VNĐ',
      quantity: 3,
      deadline: '20/11/2026',
      status: 'Đang tuyển',
      applicantCount: 35,
      description: 'Xây dựng ứng dụng mobile đa nền tảng Flutter (iOS & Android) mượt mà, tối ưu state Bloc/Provider.',
      requirements: 'Tối thiểu 2 năm kinh nghiệm Flutter/Dart, hiểu sâu về Clean Architecture.',
      benefits: 'Phụ cấp ăn trưa, môi trường Agile năng động, review lương 2 lần/năm.',
    ),
    JobPosting(
      id: 3,
      title: 'UI/UX Product Designer',
      department: 'Phòng Sản phẩm & Thiết kế',
      location: 'TP. Hồ Chí Minh',
      type: 'Toàn thời gian',
      salary: '16 - 24 Triệu VNĐ',
      quantity: 1,
      deadline: '10/11/2026',
      status: 'Đang tuyển',
      applicantCount: 19,
      description: 'Xây dựng Design System chuẩn chỉ trên Figma, tối ưu hóa hành trình người dùng User Journey.',
      requirements: 'Thành thạo Figma, tư duy User-Centric, có portfolio dự án ấn tượng.',
      benefits: 'Màn hình 4K, khám sức khỏe định kỳ, lộ trình thăng tiến rõ ràng.',
    ),
  ];

  static final List<Candidate> _mockCandidates = [
    Candidate(
      id: 101,
      applicationCode: 'APP-2026-101',
      fullName: 'Nguyễn Văn An',
      email: 'an.nguyen@gmail.com',
      phone: '0912 345 678',
      appliedJobTitle: 'Senior Java Backend Engineer',
      jobId: 1,
      stage: 'TECH_INTERVIEW',
      experience: '4.5 năm kinh nghiệm',
      education: 'ĐH Bách Khoa Hà Nội (CNTT)',
      skills: ['Java', 'Spring Boot', 'Kafka', 'Docker'],
      rating: 4.8,
      source: 'TopCV',
      applyDate: '24/10/2026',
      avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150',
      interviewTime: 'Hôm nay 09:30',
      interviewer: 'Anh Tuấn (Tech Lead)',
      notes: 'Ứng viên có kiến thức hệ thống phân tán rất vững. Thái độ tích cực.',
      testScore: '92/100 (Java Core)',
      testStatus: 'ĐÃ_CHẤM',
    ),
    Candidate(
      id: 102,
      applicationCode: 'APP-2026-102',
      fullName: 'Trần Thị Mai',
      email: 'mai.tran@outlook.com',
      phone: '0987 654 321',
      appliedJobTitle: 'Senior Flutter Mobile Developer',
      jobId: 2,
      stage: 'TECH_INTERVIEW',
      experience: '2.5 năm kinh nghiệm',
      education: 'ĐH Công nghệ - ĐHQGHN',
      skills: ['Flutter', 'Dart', 'Bloc', 'REST API'],
      rating: 4.5,
      source: 'LinkedIn',
      applyDate: '23/10/2026',
      avatarUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150',
      interviewTime: 'Hôm nay 14:00',
      interviewer: 'Anh Hưng (Mobile Lead)',
      notes: 'Hiểu sâu Bloc pattern, cẩn thận, đã phát hành 2 app lên Play Store.',
      testScore: '88/100 (Flutter UI)',
      testStatus: 'ĐÃ_CHẤM',
    ),
  ];

  static final List<InterviewSchedule> _mockInterviews = [
    InterviewSchedule(
      id: 1,
      candidateName: 'Nguyễn Văn An',
      jobTitle: 'Senior Java Backend Engineer',
      date: 'Hôm nay (27/10/2026)',
      time: '09:30 - 10:30',
      round: 'Phỏng vấn Kỹ thuật (Vòng 2)',
      interviewer: 'Anh Tuấn (Tech Lead), Anh Minh (Senior Dev)',
      format: 'Online (Google Meet)',
      meetingLink: 'https://meet.google.com/abc-xyz-ats',
      status: 'Sắp diễn ra',
      technicalScore: 4.8,
      problemSolvingScore: 4.9,
      communicationScore: 4.5,
      culturalFitScore: 4.8,
      decision: 'PASS',
    ),
    InterviewSchedule(
      id: 2,
      candidateName: 'Trần Thị Mai',
      jobTitle: 'Senior Flutter Mobile Developer',
      date: 'Hôm nay (27/10/2026)',
      time: '14:00 - 15:00',
      round: 'Phỏng vấn Kỹ thuật & Live Coding',
      interviewer: 'Chị Linh (HR) & Anh Hưng (Mobile Lead)',
      format: 'Trực tiếp tại Phòng họp 302',
      meetingLink: '',
      status: 'Sắp diễn ra',
      technicalScore: 4.6,
      problemSolvingScore: 4.4,
      communicationScore: 4.7,
      culturalFitScore: 4.5,
      decision: 'PASS',
    ),
  ];

  static final List<Map<String, dynamic>> _mockApprovals = [
    {
      'id': 1,
      'category': 'offer',
      'title': 'Phạm Quốc Bảo',
      'subtitle': 'Senior Java Backend Engineer',
      'tag': 'Thư mời nhận việc',
      'highlight': '32M VNĐ',
      'details': {
        'Thưởng gia nhập:': '10.000.000 VNĐ',
        'Ngày nhận việc:': '01/11/2026',
        'Người tạo đề xuất:': 'Thu Hà (HR Manager)',
      },
      'actionTitle': 'Duyệt Offer',
    },
    {
      'id': 2,
      'category': 'requisition',
      'title': 'Flutter Mobile Developer',
      'subtitle': 'Phòng Kỹ thuật & Công nghệ',
      'tag': 'Đề xuất tuyển dụng',
      'highlight': '3 chỉ tiêu',
      'details': {
        'Khung lương:': '18 - 28 Triệu VNĐ',
        'Độ ưu tiên:': 'Cao (Gấp)',
        'Hạn tuyển dụng:': '20/11/2026',
      },
      'actionTitle': 'Duyệt Đề Xuất',
    },
  ];
}

