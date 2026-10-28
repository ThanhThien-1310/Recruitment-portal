import 'package:flutter/material.dart';
import 'screens/interview_schedule_screen.dart';
import 'screens/approvals_screen.dart';
import 'screens/candidate_list_screen.dart';

void main() {
  runApp(const TalentFlowMobileApp());
}

class TalentFlowMobileApp extends StatelessWidget {
  const TalentFlowMobileApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'TalentFlow ATS',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF2563EB),
          primary: const Color(0xFF2563EB),
          surface: Colors.white,
        ),
        scaffoldBackgroundColor: const Color(0xFFF8FAFC),
        appBarTheme: const AppBarTheme(
          centerTitle: false,
          backgroundColor: Colors.white,
          foregroundColor: Color(0xFF0F172A),
          elevation: 0,
        ),
      ),
      home: const MainNavigationScreen(),
    );
  }
}

class MainNavigationScreen extends StatefulWidget {
  const MainNavigationScreen({super.key});

  @override
  State<MainNavigationScreen> createState() => _MainNavigationScreenState();
}

class _MainNavigationScreenState extends State<MainNavigationScreen> {
  int _currentIndex = 0;

  final List<Widget> _screens = const [
    InterviewScheduleScreen(), // Lịch PV & Scorecard
    ApprovalsScreen(),         // Phê duyệt Offer & Requisitions
    CandidateListScreen(),     // Kho hồ sơ ứng viên
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_currentIndex],
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border(top: BorderSide(color: Colors.grey.shade200)),
        ),
        child: NavigationBar(
          selectedIndex: _currentIndex,
          onDestinationSelected: (index) => setState(() => _currentIndex = index),
          backgroundColor: Colors.white,
          indicatorColor: Colors.blue.shade50,
          destinations: const [
            NavigationDestination(
              icon: Icon(Icons.calendar_today_outlined),
              selectedIcon: Icon(Icons.calendar_month, color: Color(0xFF2563EB)),
              label: 'Lịch PV',
            ),
            NavigationDestination(
              icon: Badge(
                label: Text('2'),
                child: Icon(Icons.assignment_turned_in_outlined),
              ),
              selectedIcon: Badge(
                label: Text('2'),
                child: Icon(Icons.assignment_turned_in, color: Color(0xFF2563EB)),
              ),
              label: 'Phê duyệt',
            ),
            NavigationDestination(
              icon: Icon(Icons.people_outline),
              selectedIcon: Icon(Icons.people, color: Color(0xFF2563EB)),
              label: 'Ứng viên',
            ),
          ],
        ),
      ),
    );
  }
}
