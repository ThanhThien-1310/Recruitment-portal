import 'package:flutter/material.dart';
import '../models/job_posting.dart';
import '../services/api_service.dart';

class JobListScreen extends StatefulWidget {
  const JobListScreen({super.key});

  @override
  State<JobListScreen> createState() => _JobListScreenState();
}

class _JobListScreenState extends State<JobListScreen> {
  late Future<List<JobPosting>> _jobsFuture;
  String _selectedDept = '';
  String _searchKeyword = '';

  @override
  void initState() {
    super.initState();
    _loadJobs();
  }

  void _loadJobs() {
    setState(() {
      _jobsFuture = ApiService.getJobs();
    });
  }

  void _showJobDetail(JobPosting job) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => Container(
        height: MediaQuery.of(context).size.height * 0.85,
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
        ),
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(color: Colors.grey.shade300, borderRadius: BorderRadius.circular(2)),
              ),
            ),
            const SizedBox(height: 16),
            Text(job.title, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Color(0xFF0F172A))),
            const SizedBox(height: 6),
            Text('${job.department} • ${job.location}', style: TextStyle(color: Colors.grey.shade600, fontSize: 13)),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(color: const Color(0xFFECFDF5), borderRadius: BorderRadius.circular(10)),
              child: Row(
                children: [
                  const Icon(Icons.payments_outlined, color: Color(0xFF059669), size: 20),
                  const SizedBox(width: 8),
                  Text('Mức lương: ${job.salary}', style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF059669), fontSize: 14)),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Mô Tả Công Việc', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 6),
                    Text(job.description, style: const TextStyle(fontSize: 13, height: 1.5)),
                    const SizedBox(height: 16),
                    const Text('Yêu Cầu Ứng Viên', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 6),
                    Text(job.requirements, style: const TextStyle(fontSize: 13, height: 1.5)),
                    const SizedBox(height: 16),
                    const Text('Quyền Lợi & Phúc Lợi', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 6),
                    Text(job.benefits, style: const TextStyle(fontSize: 13, height: 1.5)),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: FilledButton.icon(
                onPressed: () {
                  Navigator.pop(context);
                  _showApplyDialog(job);
                },
                icon: const Icon(Icons.send),
                label: const Text('Nộp Đơn Ứng Tuyển Ngay', style: TextStyle(fontWeight: FontWeight.bold)),
                style: FilledButton.styleFrom(
                  backgroundColor: const Color(0xFF2563EB),
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showApplyDialog(JobPosting job) {
    final nameCtrl = TextEditingController();
    final emailCtrl = TextEditingController();
    final phoneCtrl = TextEditingController();

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Ứng tuyển: ${job.title}', style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: nameCtrl, decoration: const InputDecoration(labelText: 'Họ và tên *')),
              TextField(controller: emailCtrl, decoration: const InputDecoration(labelText: 'Email *')),
              TextField(controller: phoneCtrl, decoration: const InputDecoration(labelText: 'Số điện thoại *')),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Hủy')),
          FilledButton(
            onPressed: () {
              Navigator.pop(ctx);
              final appCode = 'APP-2026-${(100 + (DateTime.now().millisecond % 900))}';
              showDialog(
                context: context,
                builder: (c) => AlertDialog(
                  icon: const Icon(Icons.check_circle, color: Colors.green, size: 48),
                  title: const Text('Nộp Hồ Sơ Thành Công!'),
                  content: Text('Mã hồ sơ của bạn là: $appCode.\nBạn có thể vào tab "Tra cứu" để theo dõi tiến trình!'),
                  actions: [FilledButton(onPressed: () => Navigator.pop(c), child: const Text('OK'))],
                ),
              );
            },
            child: const Text('Gửi Hồ Sơ'),
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
        title: const Text('Cơ Hội Nghề Nghiệp', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black87,
      ),
      body: Column(
        children: [
          // Search Bar
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              onChanged: (val) => setState(() => _searchKeyword = val.toLowerCase()),
              decoration: InputDecoration(
                hintText: 'Tìm theo chức danh (Java, Flutter...)...',
                prefixIcon: const Icon(Icons.search, size: 20),
                filled: true,
                fillColor: Colors.white,
                contentPadding: const EdgeInsets.symmetric(vertical: 12),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: Colors.grey.shade300),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: Colors.grey.shade200),
                ),
              ),
            ),
          ),

          // Department Filters
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              children: [
                _buildDeptChip('Tất cả', ''),
                _buildDeptChip('Kỹ thuật & Công nghệ', 'Kỹ thuật'),
                _buildDeptChip('Sản phẩm & Thiết kế', 'Sản phẩm'),
                _buildDeptChip('Nhân sự', 'Nhân sự'),
              ],
            ),
          ),
          const SizedBox(height: 12),

          // Jobs List
          Expanded(
            child: FutureBuilder<List<JobPosting>>(
              future: _jobsFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }

                var list = snapshot.data ?? [];
                if (_selectedDept.isNotEmpty) {
                  list = list.where((j) => j.department.contains(_selectedDept)).toList();
                }
                if (_searchKeyword.isNotEmpty) {
                  list = list.where((j) => j.title.toLowerCase().contains(_searchKeyword)).toList();
                }

                if (list.isEmpty) {
                  return const Center(child: Text('Không tìm thấy việc làm phù hợp'));
                }

                return ListView.builder(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  itemCount: list.length,
                  itemBuilder: (context, index) {
                    final job = list[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 12),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                      elevation: 0,
                      color: Colors.white,
                      child: InkWell(
                        borderRadius: BorderRadius.circular(14),
                        onTap: () => _showJobDetail(job),
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                    decoration: BoxDecoration(
                                      color: Colors.blue.shade50,
                                      borderRadius: BorderRadius.circular(6),
                                    ),
                                    child: Text(job.department,
                                        style: TextStyle(color: Colors.blue.shade700, fontSize: 11, fontWeight: FontWeight.bold)),
                                  ),
                                  Text(job.salary,
                                      style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF059669), fontSize: 13)),
                                ],
                              ),
                              const SizedBox(height: 8),
                              Text(job.title,
                                  style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF0F172A))),
                              const SizedBox(height: 6),
                              Row(
                                children: [
                                  const Icon(Icons.location_on_outlined, size: 14, color: Colors.grey),
                                  const SizedBox(width: 4),
                                  Text(job.location, style: TextStyle(fontSize: 12, color: Colors.grey.shade600)),
                                  const SizedBox(width: 14),
                                  const Icon(Icons.people_outline, size: 14, color: Colors.grey),
                                  const SizedBox(width: 4),
                                  Text('${job.applicantCount} hồ sơ', style: TextStyle(fontSize: 12, color: Colors.grey.shade600)),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDeptChip(String label, String value) {
    final isSelected = _selectedDept == value;
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: ChoiceChip(
        label: Text(label, style: TextStyle(fontSize: 12, color: isSelected ? Colors.white : Colors.black87)),
        selected: isSelected,
        selectedColor: const Color(0xFF2563EB),
        backgroundColor: Colors.white,
        onSelected: (selected) => setState(() => _selectedDept = selected ? value : ''),
      ),
    );
  }
}

