import 'dart:async';

import 'package:flutter/services.dart';

class FlutterFloatWindow {
  static const MethodChannel _channel =
      const MethodChannel('flutter_float_window');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> requestPermission() async {
    return _channel.invokeMethod('requestPermission');
  }

  static Future<bool> get isRequestFloatPermission async {
    final bool isHav = await _channel.invokeMethod('isRequestFloatPermission');
    return isHav;
  }
}
