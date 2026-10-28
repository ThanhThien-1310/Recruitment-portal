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
    text: 'Kiến trúc Microservices vững, giao tiếp tự tin, tư duy thiết kế hệ thống tốt.',
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
          size: 44,
        ),
        title: Text(isPass ? 'Đã Chấp Thuận (PASS)' : 'Đã Từ Chối (FAIL)'),
        content: Text(
          isPass
              ? 'Kết quả điểm GPA (${_averageScore.toStringAsFixed(1)}/5.0) đã được đồng bộ lên Portal ATS.'
              : 'Đã ghi nhận kết quả đánh giá cho ${widget.interview.candidateName}.',
        ),
        actions: [
          FilledButton(
            onPressed: () {
              Navigator.pop(ctx);
              Navigator.pop(context);
            },
            child: const Text('Đóng'),
          ),
        ],
      ),
    );
  }

  Widget _buildCriterionSlider({
    required String title,
    required double value,
    required ValueChanged<double> onChanged,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: Colors.grey.shade200),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF1E293B))),
              Text(
                '${value.toStringAsFixed(1)} / 5.0',
                style: const TextStyle(fontWeight: FontWeight.w900, color: Color(0xFF2563EB), fontSize: 13),
              ),
            ],
          ),
          Slider(
            value: value,
            min: 1.0,
            max: 5.0,
            divisions: 8,
            activeColor: const Color(0xFF2563EB),
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
        title: const Text('Phiếu Đánh Giá', style: TextStyle(fontWeight: FontWeight.w800, fontSize: 16)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF0F172A),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Candidate mini banner
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(14),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 20,
                    backgroundColor: Colors.blue.shade100,
                    child: Text(
                      widget.interview.candidateName.substring(0, 1),
                      style: const TextStyle(color: Color(0xFF1E3A8A), fontWeight: FontWeight.bold),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(widget.interview.candidateName,
                            style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Color(0xFF0F172A))),
                        Text(widget.interview.jobTitle,
                            style: TextStyle(fontSize: 11, color: Colors.grey.shade600)),
                      ],
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                    decoration: BoxDecoration(
                      color: const Color(0xFFEFF6FF),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Column(
                      children: [
                        const Text('GPA', style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Colors.grey)),
                        Text(
                          _averageScore.toStringAsFixed(1),
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w900, color: Color(0xFF2563EB)),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // 4 Criteria
            _buildCriterionSlider(
              title: '1. Kỹ thuật chuyên môn',
              value: _techScore,
              onChanged: (v) => setState(() => _techScore = v),
            ),
            _buildCriterionSlider(
              title: '2. Tư duy giải quyết vấn đề',
              value: _problemScore,
              onChanged: (v) => setState(() => _problemScore = v),
            ),
            _buildCriterionSlider(
              title: '3. Kỹ năng giao tiếp',
              value: _commScore,
              onChanged: (v) => setState(() => _commScore = v),
            ),
            _buildCriterionSlider(
              title: '4. Phù hợp văn hóa',
              value: _cultureScore,
              onChanged: (v) => setState(() => _cultureScore = v),
            ),

            // Notes
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(14),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Ghi chú nhận xét:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                  const SizedBox(height: 6),
                  TextField(
                    controller: _notesController,
                    maxLines: 2,
                    decoration: InputDecoration(
                      filled: true,
                      fillColor: const Color(0xFFF8FAFC),
                      contentPadding: const EdgeInsets.all(10),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8),
                        borderSide: BorderSide(color: Colors.grey.shade300),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Actions
            Row(
              children: [
                Expanded(
                  child: OutlinedButton(
                    onPressed: _isSubmitting ? null : () => _submitDecision('FAIL'),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      side: BorderSide(color: Colors.grey.shade300),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      foregroundColor: Colors.grey.shade700,
                    ),
                    child: const Text('Từ chối (FAIL)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: FilledButton(
                    onPressed: _isSubmitting ? null : () => _submitDecision('PASS'),
                    style: FilledButton.styleFrom(
                      backgroundColor: const Color(0xFF2563EB),
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                    child: const Text('Đạt (PASS)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }
}
