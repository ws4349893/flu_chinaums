#import "FluChinaumsPlugin.h"
#import <flu_chinaums/flu_chinaums-Swift.h>

@implementation FluChinaumsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFluChinaumsPlugin registerWithRegistrar:registrar];
}
@end
