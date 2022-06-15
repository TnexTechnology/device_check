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
          func msHookReturnFalse(takes: Int) -> Bool {
              return false
          }
          
          let funcAddr = getSwiftFunctionAddr(msHookReturnFalse)
          let isEmulator = IOSSecuritySuite.amIRunInEmulator()
          let isReversed = IOSSecuritySuite.amIReverseEngineered()
          let isMSHooked = IOSSecuritySuite.amIMSHooked(funcAddr)
          let jailbroken = IOSSecuritySuite.amIJailbroken()
          let isDebugged = IOSSecuritySuite.amIDebugged()
          let isHasBreakpoint = IOSSecuritySuite.hasBreakpointAt(funcAddr, functionSize: nil)
          let isWatchpoint = IOSSecuritySuite.hasWatchpoint()
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
      
//              // MSHook Check
//              func msHookReturnFalse(takes: Int) -> Bool {
//                  /// add breakpoint at here to test `IOSSecuritySuite.hasBreakpointAt`
//                  return false
//              }
//              typealias FunctionType = @convention(thin) (Int) -> (Bool)
//              func getSwiftFunctionAddr(_ function: @escaping FunctionType) -> UnsafeMutableRawPointer {
//                  return unsafeBitCast(function, to: UnsafeMutableRawPointer.self)
//              }
//              let funcAddr = getSwiftFunctionAddr(msHookReturnFalse)
//
//      let jailbreakStatus = IOSSecuritySuite.amIJailbrokenWithFailMessage()
//              let title = jailbreakStatus.jailbroken ? "Jailbroken" : "Jailed"
//              let message = """
//              Jailbreak: \(jailbreakStatus.failMessage),
//              Run in emulator?: \(IOSSecuritySuite.amIRunInEmulator())
//              Debugged?: \(IOSSecuritySuite.amIDebugged())
//              HasBreakpoint?: \(IOSSecuritySuite.hasBreakpointAt(funcAddr, functionSize: nil))
//              Has watchpoint: \(testWatchpoint())
//              Reversed?: \(IOSSecuritySuite.amIReverseEngineered())
//              Am I MSHooked: \(IOSSecuritySuite.amIMSHooked(funcAddr))
//              Am I tempered with: \(IOSSecuritySuite.amITampered([.bundleID("com.tnex.talsec.talsecExample")]).result)
//              Application executable file hash value: \(IOSSecuritySuite.getMachOFileHashValue() ?? "")
//              IOSSecuritySuite executable file hash value: \(IOSSecuritySuite.getMachOFileHashValue(.custom("IOSSecuritySuite")) ?? "")
//              Am I proxied: \(IOSSecuritySuite.amIProxied())
//              """
//    result("title " + title + " message " + message)
  }
}
