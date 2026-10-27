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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Lịch Phỏng Vấn Của Tôi', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black87,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadInterviews,
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async => _loadInterviews(),
        child: FutureBuilder<List<InterviewSchedule>>(
          future: _interviewsFuture,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Center(child: CircularProgressIndicator());
            }

            final interviews = snapshot.data ?? [];
            if (interviews.isEmpty) {
              return const Center(child: Text('Không có lịch phỏng vấn nào hôm nay'));
            }

            return ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: interviews.length,
              itemBuilder: (context, index) {
                final item = interviews[index];
                final isOnline = item.format.toLowerCase().contains('online');

                return Container(
                  margin: const EdgeInsets.only(bottom: 16),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: Colors.grey.shade200),
                    boxShadow: [
                      BoxShadow(color: Colors.black.withOpacity(0.03), blurRadius: 8, offset: const Offset(0, 2)),
                    ],
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Top badge & time
                        Row(
                          mainAxisAlignment: MainAxisAlignment.between,
                          children: [
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                              decoration: BoxDecoration(
                                color: Colors.blue.shade50,
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: Text(
                                item.round,
                                style: TextStyle(color: Colors.blue.shade700, fontSize: 12, fontWeight: FontWeight.bold),
                              ),
                            ),
                            Row(
                              children: [
                                const Icon(Icons.access_time, size: 14, color: Colors.grey),
                                const SizedBox(width: 4),
                                Text(item.time, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: Colors.black87)),
                              ],
                            ),
                          ],
                        ),
                        const SizedBox(height: 12),

                        // Candidate Name & Job
                        Text(
                          item.candidateName,
                          style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          item.jobTitle,
                          style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
                        ),
                        const SizedBox(height: 10),

                        // Format (Online or Room)
                        Row(
                          children: [
                            Icon(isOnline ? Icons.videocam : Icons.business,
                                size: 16, color: isOnline ? Colors.blue : Colors.green),
                            const SizedBox(width: 6),
                            Text(
                              item.format,
                              style: TextStyle(fontSize: 12, fontWeight: FontWeight.w500, color: isOnline ? Colors.blue.shade700 : Colors.green.shade700),
                            ),
                          ],
                        ),
                        const Divider(height: 24),

                        // Action Buttons
                        Row(
                          children: [
                            if (isOnline)
                              Expanded(
                                child: OutlinedButton.icon(
                                  onPressed: () {
                                    ScaffoldMessenger.of(context).showSnackBar(
                                      SnackBar(content: Text('Đang mở phòng họp Google Meet: ${item.meetingLink}')),
                                    );
                                  },
                                  icon: const Icon(Icons.video_call, size: 18),
                                  label: const Text('Vào Meet', style: TextStyle(fontSize: 12)),
                                  style: OutlinedButton.styleFrom(
                                    padding: const EdgeInsets.symmetric(vertical: 10),
                                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                                  ),
                                ),
                              ),
                            if (isOnline) const SizedBox(width: 10),
                            Expanded(
                              flex: isOnline ? 1 : 2,
                              child: FilledButton.icon(
                                onPressed: () {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                      builder: (context) => ScorecardScreen(interview: item),
                                    ),
                                  ).then((_) => _loadInterviews());
                                },
                                icon: const Icon(Icons.star, size: 18),
                                label: const Text('Scorecard', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                                style: FilledButton.styleFrom(
                                  backgroundColor: const Color(0xFF2563EB),
                                  padding: const EdgeInsets.symmetric(vertical: 10),
                                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                                ),
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
    );
  }
}
