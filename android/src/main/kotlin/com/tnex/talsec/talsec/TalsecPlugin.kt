package com.tnex.talsec.talsec

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** TalsecPlugin */
class TalsecPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "talsec")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "isEmulator") {
      result.success(EmulatorCheck().isEmulator(context))
    }else if (call.method == "isHook") {
      result.success(HookDetectionCheck().hookDetected(context))
    }else if (call.method == "isRooted") {
      result.success(RootedCheck().isJailBroken(context))
    }else if (call.method == "isUntrustedInstall") {
      result.success(UntrustedInstallationDetected().isUntrustedInstaller(context))
    }else if(call.method == "isDeviceNotSupported"){
      val isNotSupported = EmulatorCheck().isEmulator(context)
              || HookDetectionCheck().hookDetected(context)
              || RootedCheck().isJailBroken(context)
              || UntrustedInstallationDetected().isUntrustedInstaller(context)

      result.success(isNotSupported)
    }
    else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
