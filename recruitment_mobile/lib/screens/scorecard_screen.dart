import 'package:flutter/material.dart';
import '../models/interview_schedule.dart';
import '../services/api_service.dart';

class ScorecardScreen extends StatefulWidget {
  final InterviewSchedule interview;

  const ScorecardScreen({super.key, required this.interview});

  @override
  State<ScorecardScreen> createState() => _ScorecardScreenState();
}

class _ScorecardScreenState extends State<ScorecardScreen> {
  double _techScore = 4.5;
  double _problemScore = 4.5;
  double _commScore = 4.5;
  double _cultureScore = 4.5;
  final TextEditingController _notesController = TextEditingController(
    text: 'Ứng viên nắm chắc kiến thức chuyên môn, giao tiếp tự tin và có tư duy logic tốt.',
  );
  bool _isSubmitting = false;

  double get _averageScore =>
      (_techScore + _problemScore + _commScore + _cultureScore) / 4.0;

  void _submitDecision(String decision) async {
    setState(() => _isSubmitting = true);

    await ApiService.submitEvaluation(
      interviewId: widget.interview.id,
      techScore: _techScore,
      problemScore: _problemScore,
      commScore: _commScore,
      cultureScore: _cultureScore,
      notes: _notesController.text,
      decision: decision,
    );

    setState(() => _isSubmitting = false);

    if (!mounted) return;

    final isPass = decision == 'PASS';
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        icon: Icon(
          isPass ? Icons.check_circle : Icons.cancel,
          color: isPass ? Colors.green : Colors.red,
          size: 48,
        ),
        title: Text(isPass ? 'Đã Chấp Thuận (Pass)!' : 'Đã Từ Chối (Deny)'),
        content: Text(
          isPass
              ? 'Hệ thống đã tự động gửi email chúc mừng tới ${widget.interview.candidateName} và chuyển ứng viên sang vòng tiếp theo.'
              : 'Đã gửi email phản hồi từ chối cho ứng viên ${widget.interview.candidateName}.',
        ),
        actions: [
          FilledButton(
            onPressed: () {
              Navigator.pop(ctx);
              Navigator.pop(context);
            },
            child: const Text('Xong'),
          ),
        ],
      ),
    );
  }

  Widget _buildCriterionSlider({
    required String title,
    required String subtitle,
    required double value,
    required ValueChanged<double> onChanged,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
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
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.blue.shade50,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  '${value.toStringAsFixed(1)} / 5.0',
                  style: TextStyle(fontWeight: FontWeight.bold, color: Colors.blue.shade700),
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(subtitle, style: TextStyle(color: Colors.grey.shade600, fontSize: 12)),
          Slider(
            value: value,
            min: 1.0,
            max: 5.0,
            divisions: 8,
            activeColor: Colors.blue.shade600,
            onChanged: onChanged,
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Phiếu Chấm Điểm Scorecard', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black87,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Candidate info card
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                gradient: const LinearGradient(colors: [Color(0xFF1E3A8A), Color(0xFF2563EB)]),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 26,
                    backgroundColor: Colors.white.withOpacity(0.2),
                    child: Text(
                      widget.interview.candidateName.substring(0, 1),
                      style: const TextStyle(color: Colors.white, fontSize: 22, fontWeight: FontWeight.bold),
                    ),
                  ),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(widget.interview.candidateName,
                            style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                        const SizedBox(height: 2),
                        Text(widget.interview.jobTitle,
                            style: const TextStyle(color: Colors.white70, fontSize: 13)),
                        const SizedBox(height: 4),
                        Text('Vòng: ${widget.interview.round}',
                            style: const TextStyle(color: Colors.amberAccent, fontSize: 12, fontWeight: FontWeight.bold)),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // GPA Badge
            Center(
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(30),
                  border: Border.all(color: Colors.blue.shade300, width: 2),
                  boxShadow: [
                    BoxShadow(color: Colors.blue.withOpacity(0.08), blurRadius: 10, offset: const Offset(0, 4)),
                  ],
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.stars, color: Colors.amber, size: 22),
                    const SizedBox(width: 8),
                    Text(
                      'Điểm Trung Bình: ${_averageScore.toStringAsFixed(2)} / 5.0',
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: Color(0xFF1E293B)),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),

            // 4 Criteria Sliders
            _buildCriterionSlider(
              title: '1. Kiến Thức Kỹ Thuật Chuyên Môn',
              subtitle: 'Độ sâu về OOP, Database Indexing, Microservices, Frameworks.',
              value: _techScore,
              onChanged: (v) => setState(() => _techScore = v),
            ),
            _buildCriterionSlider(
              title: '2. Tư Duy Giải Quyết Vấn Đề (Problem Solving)',
              subtitle: 'Thuật toán, phân tích bug, thiết kế luồng xử lý edge-cases.',
              value: _problemScore,
              onChanged: (v) => setState(() => _problemScore = v),
            ),
            _buildCriterionSlider(
              title: '3. Kỹ Năng Giao Tiếp & Diễn Đạt',
              subtitle: 'Trình bày rõ ràng, lắng nghe tốt, khả năng phối hợp nhóm.',
              value: _commScore,
              onChanged: (v) => setState(() => _commScore = v),
            ),
            _buildCriterionSlider(
              title: '4. Độ Phù Hợp Văn Hóa Doanh Nghiệp',
              subtitle: 'Tinh thần chủ động, trách nhiệm, tôn trọng tập thể.',
              value: _cultureScore,
              onChanged: (v) => setState(() => _cultureScore = v),
            ),

            // Detailed Feedback Notes
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
                  const Text('Nhận Xét Tổng Hợp Của Phỏng Vấn Viên',
                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                  const SizedBox(height: 8),
                  TextField(
                    controller: _notesController,
                    maxLines: 3,
                    decoration: InputDecoration(
                      hintText: 'Nhập ghi chú chi tiết về ứng viên...',
                      filled: true,
                      fillColor: Colors.grey.shade50,
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(10),
                        borderSide: BorderSide(color: Colors.grey.shade300),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // 1-Click Action Buttons: PASS or DENY (Rule BR-18)
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _isSubmitting ? null : () => _submitDecision('DENY'),
                    icon: const Icon(Icons.close, color: Colors.red),
                    label: const Text('Deny (Từ chối)', style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      side: const BorderSide(color: Colors.red),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: FilledButton.icon(
                    onPressed: _isSubmitting ? null : () => _submitDecision('PASS'),
                    icon: const Icon(Icons.check, color: Colors.white),
                    label: const Text('Pass (Đạt Vòng Này)', style: TextStyle(fontWeight: FontWeight.bold)),
                    style: FilledButton.styleFrom(
                      backgroundColor: const Color(0xFF16A34A),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
