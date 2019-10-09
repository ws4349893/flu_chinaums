import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flu_chinaums/flu_chinaums.dart';

void main() {
  const MethodChannel channel = MethodChannel('flu_chinaums');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FluChinaums.platformVersion, '42');
  });
}
