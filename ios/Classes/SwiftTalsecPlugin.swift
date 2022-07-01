import Flutter
import UIKit
import IOSSecuritySuite

class RuntimeClass {
   @objc dynamic func runtimeModifiedFunction()-> Void {
   }
}

public class SwiftTalsecPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "talsec", binaryMessenger: registrar.messenger())
    let instance = SwiftTalsecPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }
    

    
typealias FunctionType = @convention(thin) (Int) -> (Bool)
func getSwiftFunctionAddr(_ function: @escaping FunctionType) -> UnsafeMutableRawPointer {
    return unsafeBitCast(function, to: UnsafeMutableRawPointer.self)
}
    
    
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      switch call.method {
      case "isEmulator":
          let isEmulator = IOSSecuritySuite.amIRunInEmulator()
          result(isEmulator)
          break
      case "isHook":
          func msHookReturnFalse(takes: Int) -> Bool {
              return false
          }
          
          let funcAddr = getSwiftFunctionAddr(msHookReturnFalse)
          let isReversed = IOSSecuritySuite.amIReverseEngineered()
          let isMSHooked = IOSSecuritySuite.amIMSHooked(funcAddr)
          
          if let args = call.arguments as? Dictionary<String, Any>,
            let bundleID = args["bundleID"] as? String{
            let isTempered = IOSSecuritySuite.amITampered([.bundleID(bundleID)]).result
            result(isReversed || isMSHooked || isTempered)
          }else{
              let isTempered = IOSSecuritySuite.amITampered([.bundleID("")]).result
              result(isReversed || isMSHooked || isTempered)
          }
          
          break
      case "isRooted":
          let jailbroken = IOSSecuritySuite.amIJailbroken()
          result(jailbroken)
          break
      case "isUntrustedInstall":
          func msHookReturnFalse(takes: Int) -> Bool {
              return false
          }
          
          let funcAddr = getSwiftFunctionAddr(msHookReturnFalse)
          let isDebugged = IOSSecuritySuite.amIDebugged()
          let isHasBreakpoint = IOSSecuritySuite.hasBreakpointAt(funcAddr, functionSize: nil)
          let isWatchpoint = IOSSecuritySuite.hasWatchpoint()
          result(isDebugged || isHasBreakpoint || isWatchpoint)
          break
      case "isDeviceNotSupported":
          let isEmulator = IOSSecuritySuite.amIRunInEmulator()
          let isReversed = IOSSecuritySuite.amIReverseEngineered()
          var isMSHooked = false
          var isHasBreakpoint = false
          var isWatchpoint = false
          #if arch(arm64)
                  func msHookReturnFalse(takes: Int) -> Bool {
                      return false
                  }
                  
                  let funcAddr = getSwiftFunctionAddr(msHookReturnFalse)
                  isMSHooked = IOSSecuritySuite.amIMSHooked(funcAddr)
                  isHasBreakpoint = IOSSecuritySuite.hasBreakpointAt(funcAddr, functionSize: nil)
                  isWatchpoint = IOSSecuritySuite.hasWatchpoint()
          #endif
          //let isMSHooked = IOSSecuritySuite.amIMSHooked(funcAddr)
          let jailbroken = IOSSecuritySuite.amIJailbroken()
          let isDebugged = IOSSecuritySuite.amIDebugged()
          //let isHasBreakpoint = IOSSecuritySuite.hasBreakpointAt(funcAddr, functionSize: nil)
          //let isWatchpoint = IOSSecuritySuite.hasWatchpoint()
          if let args = call.arguments as? Dictionary<String, Any>,
            let bundleID = args["bundleID"] as? String{
            let isTempered = IOSSecuritySuite.amITampered([.bundleID(bundleID)]).result
              result(isEmulator || isReversed || isMSHooked || isTempered || jailbroken || isDebugged || isHasBreakpoint || isWatchpoint)
          }else{
              let isTempered = IOSSecuritySuite.amITampered([.bundleID("")]).result
              result(isEmulator || isReversed || isMSHooked || isTempered || jailbroken || isDebugged || isHasBreakpoint || isWatchpoint)
          }
          break
      default:
          result(FlutterMethodNotImplemented)
      }
  }
}
