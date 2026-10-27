import 'package:flutter/material.dart';
import '../models/candidate.dart';
import '../services/api_service.dart';

class TrackApplicationScreen extends StatefulWidget {
  const TrackApplicationScreen({super.key});

  @override
  State<TrackApplicationScreen> createState() => _TrackApplicationScreenState();
}

class _TrackApplicationScreenState extends State<TrackApplicationScreen> {
  final TextEditingController _codeCtrl = TextEditingController(text: 'APP-2026-101');
  Candidate? _candidate;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _search();
  }

  void _search() async {
    setState(() => _isLoading = true);
    final candidates = await ApiService.getCandidates();
    final query = _codeCtrl.text.trim().toLowerCase();

    final found = candidates.firstWhere(
      (c) =>
          c.applicationCode.toLowerCase() == query ||
          c.email.toLowerCase() == query ||
          c.phone.replaceAll(' ', '').contains(query.replaceAll(' ', '')),
      orElse: () => candidates.first,
    );

    setState(() {
      _candidate = found;
      _isLoading = false;
    });
  }

  Widget _buildStepItem({
    required int stepNumber,
    required String title,
    required String subtitle,
    required bool isCompleted,
    required bool isActive,
    required bool isLast,
  }) {
    Color circleColor = Colors.grey.shade300;
    Color textColor = Colors.grey.shade600;

    if (isCompleted) {
      circleColor = const Color(0xFF16A34A);
      textColor = const Color(0xFF0F172A);
    } else if (isActive) {
      circleColor = const Color(0xFF2563EB);
      textColor = const Color(0xFF2563EB);
    }

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Column with circle and vertical line
        Column(
          children: [
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: isCompleted || isActive ? circleColor : Colors.white,
                shape: BoxShape.circle,
                border: Border.all(color: circleColor, width: 2),
                boxShadow: isActive
                    ? [BoxShadow(color: circleColor.withOpacity(0.3), blurRadius: 8, spreadRadius: 2)]
                    : null,
              ),
              child: Center(
                child: isCompleted
                    ? const Icon(Icons.check, size: 18, color: Colors.white)
                    : Text(
                        '$stepNumber',
                        style: TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.bold,
                          color: isActive ? Colors.white : Colors.grey.shade600,
                        ),
                      ),
              ),
            ),
            if (!isLast)
              Container(
                width: 2,
                height: 38,
                color: isCompleted ? const Color(0xFF16A34A) : Colors.grey.shade300,
              ),
          ],
        ),
        const SizedBox(width: 14),

        // Text details
        Expanded(
          child: Padding(
            padding: const EdgeInsets.only(top: 4, bottom: 12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: textColor)),
                const SizedBox(height: 2),
                Text(subtitle, style: TextStyle(fontSize: 12, color: Colors.grey.shade600)),
              ],
            ),
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final cand = _candidate;

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Tra Cứu Tiến Trình Hồ Sơ', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black87,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Search Bar
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 4),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: Colors.grey.shade300),
                boxShadow: [
                  BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 6, offset: const Offset(0, 2)),
                ],
              ),
              child: Row(
                children: [
                  const Icon(Icons.search, color: Colors.grey),
                  const SizedBox(width: 8),
                  Expanded(
                    child: TextField(
                      controller: _codeCtrl,
                      decoration: const InputDecoration(
                        hintText: 'Nhập mã APP-2026-101 hoặc email...',
                        border: InputBorder.none,
                      ),
                      onSubmitted: (_) => _search(),
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.arrow_forward_ios, size: 16, color: Color(0xFF2563EB)),
                    onPressed: _search,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            if (_isLoading)
              const Center(child: CircularProgressIndicator())
            else if (cand != null) ...[
              // Candidate Overview Card
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: Colors.grey.shade200),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Expanded(
                          child: Text(
                            cand.fullName,
                            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18, color: Color(0xFF0F172A)),
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                          decoration: BoxDecoration(
                            color: Colors.amber.shade50,
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(color: Colors.amber.shade200),
                          ),
                          child: Text(
                            cand.stageText,
                            style: TextStyle(color: Colors.amber.shade800, fontWeight: FontWeight.bold, fontSize: 11),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 6),
                    Text(
                      'Vị trí: ${cand.appliedJobTitle}',
                      style: TextStyle(color: Colors.blue.shade700, fontWeight: FontWeight.w600, fontSize: 13),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Mã hồ sơ: ${cand.applicationCode} • Ngày nộp: ${cand.applyDate}',
                      style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),

              // 6-Stage Stepper
              Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: Colors.grey.shade200),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Tiến Trình Ứng Tuyển', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    const SizedBox(height: 16),
                    _buildStepItem(
                      stepNumber: 1,
                      title: '1. Nộp Hồ Sơ',
                      subtitle: 'Đã hoàn tất ngày ${cand.applyDate}',
                      isCompleted: true,
                      isActive: false,
                      isLast: false,
                    ),
                    _buildStepItem(
                      stepNumber: 2,
                      title: '2. Sơ Loại CV',
                      subtitle: 'Thẩm định hồ sơ đạt 88% mức độ phù hợp',
                      isCompleted: cand.stage != 'NEW',
                      isActive: cand.stage == 'NEW',
                      isLast: false,
                    ),
                    _buildStepItem(
                      stepNumber: 3,
                      title: '3. Bài Test Năng Lực',
                      subtitle: cand.stage == 'ASSESSMENT' ? 'Cần làm bài test chuyên môn' : 'Đã đạt điểm test',
                      isCompleted: ['TECH_INTERVIEW', 'HR_INTERVIEW', 'OFFER', 'HIRED'].contains(cand.stage),
                      isActive: cand.stage == 'ASSESSMENT',
                      isLast: false,
                    ),
                    _buildStepItem(
                      stepNumber: 4,
                      title: '4. Phỏng Vấn Chuyên Môn',
                      subtitle: cand.interviewer.isNotEmpty ? 'Lịch hẹn: ${cand.interviewTime}' : 'Chờ xếp lịch',
                      isCompleted: ['OFFER', 'HIRED'].contains(cand.stage),
                      isActive: ['TECH_INTERVIEW', 'HR_INTERVIEW'].contains(cand.stage),
                      isLast: false,
                    ),
                    _buildStepItem(
                      stepNumber: 5,
                      title: '5. Thư Mời Nhận Việc (Offer)',
                      subtitle: cand.stage == 'OFFER' ? 'Đã gửi offer thỏa thuận lương' : 'Chưa đến vòng',
                      isCompleted: cand.stage == 'HIRED',
                      isActive: cand.stage == 'OFFER',
                      isLast: false,
                    ),
                    _buildStepItem(
                      stepNumber: 6,
                      title: '6. Tiếp Nhận & Onboarding',
                      subtitle: cand.stage == 'HIRED' ? 'Chào đón gia nhập TechCorp!' : 'Gia nhập công ty',
                      isCompleted: cand.stage == 'HIRED',
                      isActive: cand.stage == 'HIRED',
                      isLast: true,
                    ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

