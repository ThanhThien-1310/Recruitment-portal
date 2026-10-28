import 'package:flutter/material.dart';
import '../models/candidate.dart';
import '../services/api_service.dart';

class CandidateListScreen extends StatefulWidget {
  const CandidateListScreen({super.key});

  @override
  State<CandidateListScreen> createState() => _CandidateListScreenState();
}

class _CandidateListScreenState extends State<CandidateListScreen> {
  late Future<List<Candidate>> _candidatesFuture;
  String _selectedStage = 'ALL';
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _loadCandidates();
  }

  void _loadCandidates() {
    setState(() {
      _candidatesFuture = ApiService.getCandidates();
    });
  }

  void _showCvBottomSheet(Candidate c) {
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
              // Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(c.fullName, style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold)),
                      Text(c.appliedJobTitle, style: const TextStyle(fontSize: 12, color: Color(0xFF2563EB), fontWeight: FontWeight.w600)),
                    ],
                  ),
                  IconButton(
                    icon: const Icon(Icons.close),
                    onPressed: () => Navigator.pop(context),
                  ),
                ],
              ),
              const Divider(height: 20),

              // Match Rate & Experience
              Row(
                children: [
                  Expanded(
                    child: Container(
                      padding: const EdgeInsets.all(10),
                      decoration: BoxDecoration(
                        color: Colors.blue.shade50,
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Độ phù hợp AI:', style: TextStyle(fontSize: 10, color: Colors.grey)),
                          const SizedBox(height: 2),
                          Text('${(c.rating * 20).toInt()}% Match', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.blue.shade800)),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Container(
                      padding: const EdgeInsets.all(10),
                      decoration: BoxDecoration(
                        color: const Color(0xFFF1F5F9),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Kinh nghiệm:', style: TextStyle(fontSize: 10, color: Colors.grey)),
                          const SizedBox(height: 2),
                          Text(c.experience, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Color(0xFF0F172A))),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 14),

              // Education
              const Text('Học vấn', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey)),
              const SizedBox(height: 2),
              Text(c.education, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: Color(0xFF1E293B))),
              const SizedBox(height: 12),

              // Skills
              const Text('Kỹ năng chính', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey)),
              const SizedBox(height: 6),
              Wrap(
                spacing: 6,
                runSpacing: 6,
                children: c.skills.map((s) {
                  return Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(
                      color: const Color(0xFFF1F5F9),
                      borderRadius: BorderRadius.circular(6),
                    ),
                    child: Text(s, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF334155))),
                  );
                }).toList(),
              ),
              const SizedBox(height: 20),

              // PDF Button
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  onPressed: () {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Đang mở file CV PDF gốc...')),
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
        title: const Text('Kho Ứng Viên', style: TextStyle(fontWeight: FontWeight.w800, fontSize: 18)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF0F172A),
      ),
      body: Column(
        children: [
          // Search & Filter
          Container(
            color: Colors.white,
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 12),
            child: Column(
              children: [
                // Search Input
                TextField(
                  onChanged: (val) => setState(() => _searchQuery = val.toLowerCase()),
                  decoration: InputDecoration(
                    hintText: 'Tìm ứng viên, vị trí, kỹ năng...',
                    hintStyle: TextStyle(fontSize: 12, color: Colors.grey.shade400),
                    prefixIcon: const Icon(Icons.search, size: 18, color: Colors.grey),
                    isDense: true,
                    contentPadding: const EdgeInsets.symmetric(vertical: 10),
                    filled: true,
                    fillColor: const Color(0xFFF1F5F9),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: BorderSide.none,
                    ),
                  ),
                ),
                const SizedBox(height: 10),

                // Stage Pills
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      _buildStageChip('ALL', 'Tất cả'),
                      _buildStageChip('TECH_INTERVIEW', 'Phỏng vấn'),
                      _buildStageChip('ASSESSMENT', 'Bài test'),
                      _buildStageChip('OFFER', 'Offer'),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const Divider(height: 1, color: Color(0xFFE2E8F0)),

          // Candidate List
          Expanded(
            child: FutureBuilder<List<Candidate>>(
              future: _candidatesFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }

                var list = snapshot.data ?? [];
                if (_selectedStage != 'ALL') {
                  list = list.where((c) => c.stage == _selectedStage).toList();
                }
                if (_searchQuery.isNotEmpty) {
                  list = list.where((c) {
                    final str = '${c.fullName} ${c.appliedJobTitle} ${c.skills.join(" ")}'.toLowerCase();
                    return str.contains(_searchQuery);
                  }).toList();
                }

                if (list.isEmpty) {
                  return const Center(child: Text('Không tìm thấy ứng viên nào', style: TextStyle(color: Colors.grey)));
                }

                return ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: list.length,
                  itemBuilder: (context, index) {
                    final c = list[index];
                    return GestureDetector(
                      onTap: () => _showCvBottomSheet(c),
                      child: Container(
                        margin: const EdgeInsets.only(bottom: 12),
                        padding: const EdgeInsets.all(14),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(14),
                          border: Border.all(color: Colors.grey.shade200),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withOpacity(0.02),
                              blurRadius: 6,
                              offset: const Offset(0, 2),
                            ),
                          ],
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Row(
                                  children: [
                                    CircleAvatar(
                                      radius: 18,
                                      backgroundImage: NetworkImage(c.avatarUrl),
                                    ),
                                    const SizedBox(width: 10),
                                    Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(c.fullName, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
                                        Text(c.appliedJobTitle, style: TextStyle(fontSize: 11, color: Colors.grey.shade600)),
                                      ],
                                    ),
                                  ],
                                ),
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 3),
                                  decoration: BoxDecoration(
                                    color: Colors.blue.shade50,
                                    borderRadius: BorderRadius.circular(6),
                                  ),
                                  child: Text(
                                    '${(c.rating * 20).toInt()}% Match',
                                    style: TextStyle(fontSize: 10, fontWeight: FontWeight.w800, color: Colors.blue.shade700),
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 10),
                            const Divider(height: 1, color: Color(0xFFF1F5F9)),
                            const SizedBox(height: 8),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text('Vòng: ${c.stageText}', style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF334155))),
                                Text(c.experience, style: TextStyle(fontSize: 11, color: Colors.grey.shade600)),
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
        ],
      ),
    );
  }

  Widget _buildStageChip(String key, String label) {
    final active = _selectedStage == key;
    return GestureDetector(
      onTap: () => setState(() => _selectedStage = key),
      child: Container(
        margin: const EdgeInsets.only(right: 8),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          color: active ? const Color(0xFF2563EB) : Colors.white,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: active ? const Color(0xFF2563EB) : Colors.grey.shade300),
        ),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 11,
            fontWeight: FontWeight.bold,
            color: active ? Colors.white : Colors.grey.shade700,
          ),
        ),
      ),
    );
  }
}
