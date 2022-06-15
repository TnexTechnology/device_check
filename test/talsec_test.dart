import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:talsec/talsec.dart';

void main() {
  const MethodChannel channel = MethodChannel('talsec');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  // test('getPlatformVersion', () async {
  //   expect(await Talsec.platformVersion, '42');
  // });
}
