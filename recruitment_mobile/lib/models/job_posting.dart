class JobPosting {
  final int id;
  final String title;
  final String department;
  final String location;
  final String type;
  final String salary;
  final int quantity;
  final String deadline;
  final String status;
  final int applicantCount;
  final String description;
  final String requirements;
  final String benefits;

  JobPosting({
    required this.id,
    required this.title,
    required this.department,
    required this.location,
    required this.type,
    required this.salary,
    required this.quantity,
    required this.deadline,
    required this.status,
    required this.applicantCount,
    required this.description,
    required this.requirements,
    required this.benefits,
  });

  factory JobPosting.fromJson(Map<String, dynamic> json) {
    return JobPosting(
      id: json['id'] ?? 0,
      title: json['title'] ?? '',
      department: json['department'] ?? '',
      location: json['location'] ?? '',
      type: json['type'] ?? 'Toàn thời gian',
      salary: json['salary'] ?? '',
      quantity: json['quantity'] ?? 1,
      deadline: json['deadline'] ?? '',
      status: json['status'] ?? 'Đang tuyển',
      applicantCount: json['applicantCount'] ?? 0,
      description: json['description'] ?? '',
      requirements: json['requirements'] ?? '',
      benefits: json['benefits'] ?? '',
    );
  }
}

