
import 'dart:async';

import 'package:flutter/services.dart';

class Talsec {
  static const MethodChannel _channel = MethodChannel('talsec');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> get isEmulator async {
    final bool isEmulator = await _channel.invokeMethod('isEmulator');
    return isEmulator;
  }

  static Future<bool> get isHook async {
    final bool isHook = await _channel.invokeMethod('isHook');
    return isHook;
  }

  static Future<bool> get isRooted async {
    final bool isRooted = await _channel.invokeMethod('isRooted');
    return isRooted;
  }

  static Future<bool> get isUntrustedInstall async {
    final bool isUntrustedInstall = await _channel.invokeMethod('isUntrustedInstall');
    return isUntrustedInstall;
  }

  static Future<bool> get isDeviceNotSupported async {
    final bool isDeviceNotSupported = await _channel.invokeMethod('isDeviceNotSupported');
    return isDeviceNotSupported;
  }
}
