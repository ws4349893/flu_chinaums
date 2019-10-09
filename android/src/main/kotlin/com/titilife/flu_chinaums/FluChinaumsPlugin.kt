package com.titilife.flu_chinaums
import android.content.Context
import com.chinaums.pppay.unify.UnifyPayPlugin
import com.chinaums.pppay.unify.UnifyPayRequest
import com.tencent.mm.opensdk.modelpay.PayReq
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import org.json.JSONObject

class FluChinaumsPlugin: MethodCallHandler {
   companion object {
    var context : Context? = null
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      context = registrar.context()
      val channel = MethodChannel(registrar.messenger(), "flu_chinaums")
      channel.setMethodCallHandler(FluChinaumsPlugin())
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {

    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "payWx") {
      pay(call,result)
    } else {
      result.notImplemented()
    }
  }



  private fun pay(call: MethodCall, result: Result) {
    val request = PayReq()
    request.appId = call.argument("appId")
    request.partnerId = call.argument("partnerId")
    request.prepayId = call.argument("prepayId")
    request.packageValue = call.argument("packageValue")
    request.nonceStr = call.argument("nonceStr")
    request.timeStamp = call.argument<Long>("timeStamp").toString()
    request.sign = call.argument("sign")
    request.signType = call.argument("signType")
    request.extData = call.argument("extData")


    var json = JSONObject()
    json.put("appId",request.appId )
    json.put("partnerId",request.partnerId )
    json.put("prepayId",request.prepayId )
    json.put("packageValue",request.packageValue )
    json.put("nonceStr",request.nonceStr )
    json.put("timeStamp",request.timeStamp )
    json.put("sign",request.sign )
    json.put("signType",request.signType )
    json.put("extData",request.extData )

    val msg = UnifyPayRequest()
    msg.payChannel = UnifyPayRequest.CHANNEL_WEIXIN
    msg.payData = json.toString()
    UnifyPayPlugin.getInstance(context).sendPayRequest(msg)

    result.success( mapOf("platform" to "android", "result" to true))
  }
}
