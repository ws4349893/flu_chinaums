#import "FluChinaumsPlugin.h"
#import <flu_chinaums/flu_chinaums-Swift.h>


@implementation FluChinaumsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFluChinaumsPlugin registerWithRegistrar:registrar];
}

- (void)handleMethodCall:(FlutterMethodCall *)call result:(FlutterResult)result{
    if ([@"payWithUms" isEqualToString:call.method]) {
        
        
        NSNumber *appId =  call.arguments[@"appId"];
        NSNumber *timestamp =  call.arguments[@"timeStamp"];
        NSString *partnerId = call.arguments[@"partnerId"];
        NSString *prepayId = call.arguments[@"prepayId"];
        
        NSString *packageValue = call.arguments[@"packageValue"];
        NSString *nonceStr = call.arguments[@"nonceStr"];
        UInt32 timeStamp = [timestamp unsignedIntValue];
        NSString *sign = call.arguments[@"sign"];
        NSString *signType = call.arguments[@"signType"];
        NSString *extData = call.arguments[@"extData"];
    
           NSDictionary *dictionary = [NSDictionary dictionaryWithObjectsAndKeys: appId,@"appId", partnerId,@"partnerId", prepayId,@"prepayId", packageValue,@"packageValue", nonceStr,@"nonceStr", timeStamp,@"timeStamp",sign,@"sign",signType,@"signType",extData,@"extData", nil];
        NSString *payData = [self convertToJsonData:dictionary];

       
        [UMSPPPayUnifyPayPlugin payWithPayChannel: CHANNEL_WEIXIN
                                          payData:payData
                                    callbackBlock:^(NSString *resultCode, NSString *resultInfo) {
                                        
                                    }];
        
        
        /*
         */
        return;
    }
}


-(NSString *)convertToJsonData:(NSDictionary *)dict{
    
    NSError *error;
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
    
    NSString *jsonString;
    
    if (!jsonData) {
        
        NSLog(@"%@",error);
        
    }else{
        
        jsonString = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];
        
    }
    
    NSMutableString *mutStr = [NSMutableString stringWithString:jsonString];
    
    NSRange range = {0,jsonString.length};
    
    //去掉字符串中的空格
    
    [mutStr replaceOccurrencesOfString:@" " withString:@"" options:NSLiteralSearch range:range];
    
    NSRange range2 = {0,mutStr.length};
    
    //去掉字符串中的换行符
    
    [mutStr replaceOccurrencesOfString:@"\n" withString:@"" options:NSLiteralSearch range:range2];
    
    return mutStr;
    
}
@end
