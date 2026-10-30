import 'package:flutter/material.dart';
import '../models/interview_schedule.dart';
import '../services/api_service.dart';
import 'scorecard_screen.dart';

class InterviewScheduleScreen extends StatefulWidget {
  const InterviewScheduleScreen({super.key});

  @override
  State<InterviewScheduleScreen> createState() => _InterviewScheduleScreenState();
}

class _InterviewScheduleScreenState extends State<InterviewScheduleScreen> {
  late Future<List<InterviewSchedule>> _interviewsFuture;
  String _selectedFilter = 'today'; // today, week, done

  @override
  void initState() {
    super.initState();
    _loadInterviews();
  }

  void _loadInterviews() {
    setState(() {
      _interviewsFuture = ApiService.getInterviews();
    });
  }

  void _showCvQuickView(InterviewSchedule item) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (context) {
        return Padding(
          padding: EdgeInsets.only(
            left: 20,
            right: 20,
            top: 20,
            bottom: MediaQuery.of(context).viewInsets.bottom + 24,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(item.candidateName, style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold)),
                      Text(item.jobTitle, style: const TextStyle(fontSize: 12, color: Color(0xFF2563EB), fontWeight: FontWeight.w600)),
                    ],
                  ),
                  IconButton(
                    icon: const Icon(Icons.close),
                    onPressed: () => Navigator.pop(context),
                  ),
                ],
              ),
              const Divider(height: 20),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.blue.shade50,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text('Độ tương thích JD:', style: TextStyle(fontSize: 12, color: Colors.grey)),
                    Text('${(item.technicalScore * 20).toInt()}% Match', style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Colors.blue.shade900)),
                  ],
                ),
              ),
              const SizedBox(height: 14),
              const Text('Người phỏng vấn cùng:', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey)),
              const SizedBox(height: 2),
              Text(item.interviewer, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Color(0xFF1E293B))),
              const SizedBox(height: 20),
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  onPressed: () {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Đang mở file CV PDF của ${item.candidateName}...')),
                    );
                  },
                  icon: const Icon(Icons.picture_as_pdf, size: 18),
                  label: const Text('Mở File CV Gốc (PDF)', style: TextStyle(fontWeight: FontWeight.bold)),
                  style: FilledButton.styleFrom(
                    backgroundColor: const Color(0xFF0F172A),
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Lịch Phỏng Vấn', style: TextStyle(fontWeight: FontWeight.w800, fontSize: 18)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF0F172A),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadInterviews,
          ),
        ],
      ),
      body: Column(
        children: [
          // Filter Tabs
          Container(
            color: Colors.white,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Row(
              children: [
                _buildFilterButton('today', 'Hôm nay (3)'),
                const SizedBox(width: 8),
                _buildFilterButton('week', 'Tuần này (7)'),
                const SizedBox(width: 8),
                _buildFilterButton('done', 'Đã chấm'),
              ],
            ),
          ),
          const Divider(height: 1, color: Color(0xFFE2E8F0)),

          // List View
          Expanded(
            child: RefreshIndicator(
              onRefresh: () async => _loadInterviews(),
              child: FutureBuilder<List<InterviewSchedule>>(
                future: _interviewsFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  final interviews = snapshot.data ?? [];
                  if (interviews.isEmpty) {
                    return const Center(child: Text('Không có ca phỏng vấn nào', style: TextStyle(color: Colors.grey)));
                  }

                  return ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: interviews.length,
                    itemBuilder: (context, index) {
                      final item = interviews[index];
                      final isOnline = item.format.toLowerCase().contains('online');

                      return Container(
                        margin: const EdgeInsets.only(bottom: 14),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(16),
                          border: Border.all(color: Colors.grey.shade200),
                          boxShadow: [
                            BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 8, offset: const Offset(0, 2)),
                          ],
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              // Top Tag & Time
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                    decoration: BoxDecoration(
                                      color: Colors.blue.shade50,
                                      borderRadius: BorderRadius.circular(6),
                                    ),
                                    child: Text(
                                      item.time,
                                      style: TextStyle(color: Colors.blue.shade700, fontSize: 11, fontWeight: FontWeight.w800),
                                    ),
                                  ),
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                    decoration: BoxDecoration(
                                      color: isOnline ? const Color(0xFFECFDF5) : Colors.amber.shade50,
                                      borderRadius: BorderRadius.circular(6),
                                      border: Border.all(
                                        color: isOnline ? const Color(0xFFA7F3D0) : Colors.amber.shade200,
                                      ),
                                    ),
                                    child: Row(
                                      children: [
                                        Icon(isOnline ? Icons.videocam : Icons.business, size: 12, color: isOnline ? const Color(0xFF047857) : Colors.amber.shade800),
                                        const SizedBox(width: 4),
                                        Text(
                                          isOnline ? 'Google Meet' : 'Trực tiếp P.302',
                                          style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: isOnline ? const Color(0xFF047857) : Colors.amber.shade800),
                                        ),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 10),

                              // Candidate Name & Job
                              Text(
                                item.candidateName,
                                style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
                              ),
                              const SizedBox(height: 2),
                              Text(
                                item.jobTitle,
                                style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                              ),
                              const SizedBox(height: 10),

                              // Round
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                decoration: BoxDecoration(
                                  color: const Color(0xFFF8FAFC),
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Text('Vòng: ${item.round}', style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w600, color: Color(0xFF334155))),
                                    const Text('APP-2026-101', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF2563EB))),
                                  ],
                                ),
                              ),
                              const SizedBox(height: 12),

                              // Actions
                              Row(
                                children: [
                                  Expanded(
                                    child: OutlinedButton(
                                      onPressed: () => _showCvQuickView(item),
                                      style: OutlinedButton.styleFrom(
                                        padding: const EdgeInsets.symmetric(vertical: 10),
                                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                        foregroundColor: Colors.grey.shade800,
                                      ),
                                      child: const Text('Xem CV', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                                    ),
                                  ),
                                  const SizedBox(width: 8),
                                  Expanded(
                                    child: FilledButton(
                                      onPressed: () {
                                        Navigator.push(
                                          context,
                                          MaterialPageRoute(
                                            builder: (context) => ScorecardScreen(interview: item),
                                          ),
                                        ).then((_) => _loadInterviews());
                                      },
                                      style: FilledButton.styleFrom(
                                        backgroundColor: const Color(0xFF2563EB),
                                        padding: const EdgeInsets.symmetric(vertical: 10),
                                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                      ),
                                      child: const Text('Chấm điểm', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  );
                },
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterButton(String key, String label) {
    final active = _selectedFilter == key;
    return Expanded(
      child: GestureDetector(
        onTap: () => setState(() => _selectedFilter = key),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 6),
          decoration: BoxDecoration(
            color: active ? const Color(0xFFEFF6FF) : Colors.transparent,
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: active ? const Color(0xFFBFDBFE) : Colors.transparent),
          ),
          alignment: Alignment.center,
          child: Text(
            label,
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.bold,
              color: active ? const Color(0xFF2563EB) : Colors.grey.shade600,
            ),
          ),
        ),
      ),
    );
  }
}
