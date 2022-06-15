#import "TalsecPlugin.h"
#if __has_include(<talsec/talsec-Swift.h>)
#import <talsec/talsec-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "talsec-Swift.h"
#endif

@implementation TalsecPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTalsecPlugin registerWithRegistrar:registrar];
}
@end
