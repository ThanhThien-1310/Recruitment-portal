import 'package:flutter_test/flutter_test.dart';
import 'package:recruitment_mobile/main.dart';

void main() {
  testWidgets('TalentFlow app loads', (WidgetTester tester) async {
    await tester.pumpWidget(const TalentFlowMobileApp());
    expect(find.text('TalentFlow ATS'), findsNothing);
  });
}
