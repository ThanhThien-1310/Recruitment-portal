class InterviewSchedule {
  final int id;
  final String candidateName;
  final String jobTitle;
  final String date;
  final String time;
  final String round;
  final String interviewer;
  final String format;
  final String meetingLink;
  final String status;
  final double technicalScore;
  final double problemSolvingScore;
  final double communicationScore;
  final double culturalFitScore;
  final String decision;

  InterviewSchedule({
    required this.id,
    required this.candidateName,
    required this.jobTitle,
    required this.date,
    required this.time,
    required this.round,
    required this.interviewer,
    required this.format,
    required this.meetingLink,
    required this.status,
    required this.technicalScore,
    required this.problemSolvingScore,
    required this.communicationScore,
    required this.culturalFitScore,
    required this.decision,
  });

  factory InterviewSchedule.fromJson(Map<String, dynamic> json) {
    return InterviewSchedule(
      id: json['id'] ?? 0,
      candidateName: json['candidateName'] ?? '',
      jobTitle: json['jobTitle'] ?? '',
      date: json['date'] ?? '',
      time: json['time'] ?? '',
      round: json['round'] ?? 'Phỏng vấn',
      interviewer: json['interviewer'] ?? '',
      format: json['format'] ?? 'Online',
      meetingLink: json['meetingLink'] ?? '',
      status: json['status'] ?? 'Sắp diễn ra',
      technicalScore: (json['technicalScore'] is num) ? (json['technicalScore'] as num).toDouble() : 4.5,
      problemSolvingScore: (json['problemSolvingScore'] is num) ? (json['problemSolvingScore'] as num).toDouble() : 4.5,
      communicationScore: (json['communicationScore'] is num) ? (json['communicationScore'] as num).toDouble() : 4.5,
      culturalFitScore: (json['culturalFitScore'] is num) ? (json['culturalFitScore'] as num).toDouble() : 4.5,
      decision: json['decision'] ?? 'PASS',
    );
  }

  double get averageScore {
    return (technicalScore + problemSolvingScore + communicationScore + culturalFitScore) / 4.0;
  }
}
