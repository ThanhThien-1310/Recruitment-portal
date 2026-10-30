import 'package:flutter/material.dart';
import '../services/api_service.dart';

class ApprovalsScreen extends StatefulWidget {
  const ApprovalsScreen({super.key});

  @override
  State<ApprovalsScreen> createState() => _ApprovalsScreenState();
}

class _ApprovalsScreenState extends State<ApprovalsScreen> {
  String _selectedCategory = 'all'; // all, offer, requisition
  List<Map<String, dynamic>> _items = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadApprovals();
  }

  void _loadApprovals() async {
    final list = await ApiService.getApprovals();
    if (mounted) {
      setState(() {
        _items = List.from(list);
        _isLoading = false;
      });
    }
  }

  void _handleAction(int id, String action, String name) async {
    setState(() {
      _items.removeWhere((item) => item['id'] == id);
    });

    await ApiService.submitApprovalAction(id, action);

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Đã thực hiện: $action cho "$name"'),
          behavior: SnackBarBehavior.floating,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final filtered = _selectedCategory == 'all'
        ? _items
        : _items.where((i) => i['category'] == _selectedCategory).toList();

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Yêu Cầu Phê Duyệt', style: TextStyle(fontWeight: FontWeight.w800, fontSize: 18)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF0F172A),
      ),
      body: Column(
        children: [
          // Filter Tabs
          Container(
            color: Colors.white,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Row(
              children: [
                _buildFilterButton('all', 'Tất cả (${_items.length})'),
                const SizedBox(width: 8),
                _buildFilterButton('offer', 'Offer'),
                const SizedBox(width: 8),
                _buildFilterButton('requisition', 'Nhu cầu'),
              ],
            ),
          ),
          const Divider(height: 1, color: Color(0xFFE2E8F0)),

          // Items List
          Expanded(
            child: _isLoading ? const Center(child: CircularProgressIndicator()) : filtered.isEmpty
                ? const Center(
                    child: Text('Không có yêu cầu chờ duyệt', style: TextStyle(color: Colors.grey)),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: filtered.length,
                    itemBuilder: (context, index) {
                      final item = filtered[index];
                      final isOffer = item['category'] == 'offer';

                      return Container(
                        margin: const EdgeInsets.only(bottom: 14),
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(16),
                          border: Border.all(color: Colors.grey.shade200),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withOpacity(0.02),
                              blurRadius: 8,
                              offset: const Offset(0, 2),
                            ),
                          ],
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // Top Tag & Highlight
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                  decoration: BoxDecoration(
                                    color: isOffer ? Colors.amber.shade50 : Colors.blue.shade50,
                                    borderRadius: BorderRadius.circular(6),
                                    border: Border.all(
                                      color: isOffer ? Colors.amber.shade200 : Colors.blue.shade200,
                                    ),
                                  ),
                                  child: Text(
                                    item['tag'],
                                    style: TextStyle(
                                      fontSize: 11,
                                      fontWeight: FontWeight.w800,
                                      color: isOffer ? Colors.amber.shade900 : Colors.blue.shade700,
                                    ),
                                  ),
                                ),
                                Text(
                                  item['highlight'],
                                  style: TextStyle(
                                    fontSize: 13,
                                    fontWeight: FontWeight.w900,
                                    color: isOffer ? Colors.green.shade700 : Colors.blue.shade700,
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 8),

                            // Title & Subtitle
                            Text(
                              item['title'],
                              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
                            ),
                            const SizedBox(height: 2),
                            Text(
                              item['subtitle'],
                              style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                            ),
                            const SizedBox(height: 10),

                            // Key-Value Box
                            Container(
                              padding: const EdgeInsets.all(10),
                              decoration: BoxDecoration(
                                color: const Color(0xFFF8FAFC),
                                borderRadius: BorderRadius.circular(10),
                                border: Border.all(color: Colors.grey.shade100),
                              ),
                              child: Column(
                                children: (item['details'] as List<Map<String, String>>).map((d) {
                                  return Padding(
                                    padding: const EdgeInsets.symmetric(vertical: 2),
                                    child: Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: [
                                        Text(d['label']!, style: TextStyle(fontSize: 11, color: Colors.grey.shade600)),
                                        Text(d['value']!, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF1E293B))),
                                      ],
                                    ),
                                  );
                                }).toList(),
                              ),
                            ),
                            const SizedBox(height: 12),

                            // Actions
                            Row(
                              children: [
                                Expanded(
                                  child: OutlinedButton(
                                    onPressed: () => _handleAction(item['id'], 'Từ chối', item['title']),
                                    style: OutlinedButton.styleFrom(
                                      padding: const EdgeInsets.symmetric(vertical: 10),
                                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                      foregroundColor: Colors.grey.shade700,
                                    ),
                                    child: const Text('Từ chối', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                                  ),
                                ),
                                const SizedBox(width: 8),
                                Expanded(
                                  child: FilledButton(
                                    onPressed: () => _handleAction(item['id'], 'Duyệt', item['title']),
                                    style: FilledButton.styleFrom(
                                      backgroundColor: isOffer ? const Color(0xFF059669) : const Color(0xFF2563EB),
                                      padding: const EdgeInsets.symmetric(vertical: 10),
                                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                    ),
                                    child: Text(item['actionTitle'], style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterButton(String key, String label) {
    final active = _selectedCategory == key;
    return Expanded(
      child: GestureDetector(
        onTap: () => setState(() => _selectedCategory = key),
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
