class Candidate {
  final int id;
  final String applicationCode;
  final String fullName;
  final String email;
  final String phone;
  final String appliedJobTitle;
  final int jobId;
  final String stage;
  final String experience;
  final String education;
  final List<String> skills;
  final double rating;
  final String source;
  final String applyDate;
  final String avatarUrl;
  final String interviewTime;
  final String interviewer;
  final String notes;
  final String testScore;
  final String testStatus;

  Candidate({
    required this.id,
    required this.applicationCode,
    required this.fullName,
    required this.email,
    required this.phone,
    required this.appliedJobTitle,
    required this.jobId,
    required this.stage,
    required this.experience,
    required this.education,
    required this.skills,
    required this.rating,
    required this.source,
    required this.applyDate,
    required this.avatarUrl,
    required this.interviewTime,
    required this.interviewer,
    required this.notes,
    required this.testScore,
    required this.testStatus,
  });

  factory Candidate.fromJson(Map<String, dynamic> json) {
    return Candidate(
      id: json['id'] ?? 0,
      applicationCode: json['applicationCode'] ?? '',
      fullName: json['fullName'] ?? '',
      email: json['email'] ?? '',
      phone: json['phone'] ?? '',
      appliedJobTitle: json['appliedJobTitle'] ?? '',
      jobId: json['jobId'] ?? 0,
      stage: json['stage'] ?? 'NEW',
      experience: json['experience'] ?? '',
      education: json['education'] ?? '',
      skills: (json['skills'] as List<dynamic>?)?.map((e) => e.toString()).toList() ?? [],
      rating: (json['rating'] is num) ? (json['rating'] as num).toDouble() : 4.0,
      source: json['source'] ?? '',
      applyDate: json['applyDate'] ?? '',
      avatarUrl: json['avatarUrl'] ?? '',
      interviewTime: json['interviewTime'] ?? '',
      interviewer: json['interviewer'] ?? '',
      notes: json['notes'] ?? '',
      testScore: json['testScore'] ?? '',
      testStatus: json['testStatus'] ?? '',
    );
  }

  String get stageText {
    switch (stage) {
      case 'NEW':
        return 'Mới ứng tuyển';
      case 'SCREENING':
        return 'Sơ loại CV';
      case 'ASSESSMENT':
        return 'Làm bài test';
      case 'TECH_INTERVIEW':
        return 'PV Kỹ thuật';
      case 'HR_INTERVIEW':
        return 'PV HR';
      case 'OFFER':
        return 'Thư mời Offer';
      case 'HIRED':
        return 'Đã nhận việc';
      default:
        return stage;
    }
  }
}
