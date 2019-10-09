import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class FluChinaums {
  static const MethodChannel _channel =
      const MethodChannel('flu_chinaums');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// params are from server
  Future pay(
      {@required String appId,
        @required String partnerId,
        @required String prepayId,
        @required String packageValue,
        @required String nonceStr,
        @required int timeStamp,
        @required String sign,
        String signType,
        String extData}) async {
    return await _channel.invokeMethod("payWx", {
      "appId": appId,
      "partnerId": partnerId,
      "prepayId": prepayId,
      "packageValue": packageValue,
      "nonceStr": nonceStr,
      "timeStamp": timeStamp,
      "sign": sign,
      "signType": signType,
      "extData": extData,
    });
  }
}
