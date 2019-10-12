//
//  UMSPPPayUnifyPayPlugin.h
//  UMSPosPay
//
//  Created by SunXP on 17/4/25.
//  Copyright © 2017年 ChinaUMS. All rights reserved.
//

#import <Foundation/Foundation.h>

FOUNDATION_EXTERN NSString *const CHANNEL_WEIXIN;
FOUNDATION_EXTERN NSString *const CHANNEL_ALIPAY;
FOUNDATION_EXTERN NSString *const CHANNEL_UMSPAY;

typedef void(^TransactionResultBlock)(NSString *resultCode,  NSString *resultInfo);

@interface UMSPPPayUnifyPayPlugin : NSObject

/**
 *  商户下单支付接口
 *
 *  @param payChannel   支付渠道
 *  @param payData       订单信息:appPayRequest对应的json字符串
 *  @param callbackBlock 交易结果回调Block
 */
+ (void)payWithPayChannel:(NSString *)payChannel payData:(NSString *)payData callbackBlock:(TransactionResultBlock)callbackBlock;

/**
 云闪付下单接口

 @param schemes url schemes
 @param payData 订单信息:appPayRequest对应的json字符串
 @param viewController 启动支付控件的viewController
 @param callbackBlock 交易结果回调Block
 */
+ (void)cloudPayWithURLSchemes:(NSString *)schemes
                       payData:(NSString *)payData
                viewController:(UIViewController *)viewController
                 callbackBlock:(TransactionResultBlock)callbackBlock;

/**
 *  微信支付配置参数
 *
 *  @param appId   商户注册的微信支付appId
  *  @return YES：成功 NO：失败
 *  需在AppDelegate的didFinishLaunchingWithOptions方法中调用
 */
+ (BOOL)registerApp:(NSString *)appId;

/**
 *  微信支付配置参数
 *
 *  @param url   App处理的openUrl
 *  @return YES：成功 NO：失败
 *  需在AppDelegate中的方法中调用：
 *  iOS9.0之前版本：- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
 *                          - (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
 *  iOS9.0之后版本：- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options
 */
+ (BOOL)handleOpenURL:(NSURL *)url;


/**
 云闪付处理

 @param url App处理的openUrl
 @return YES：成功 NO：失败
 */
+ (BOOL)cloudPayHandleOpenURL:(NSURL *)url;

@end
