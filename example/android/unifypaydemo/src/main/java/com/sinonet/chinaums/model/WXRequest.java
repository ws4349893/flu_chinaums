package com.sinonet.chinaums.model;

/**
 * Created by xiulu on 2017/5/24.
 */
public class WXRequest {
    public String tid;
    public String msgSrc;
    public String requestTimestamp;
    public String merOrderId;
    public String mid;
    public String msgType;
    public String totalAmount;
    /**
     * 机构商户号
     */
    public String instMid;
    public String tradeType;
    public String sign;
    public String msgId;
    public String secureTransaction;
    /**
     * 商户想定制化展示的内容，长度不大于255
     */
    public String srcReserve;
    public String subAppId;

    /**
     * 分账标记
     */
    public String divisionFlag;
    /**
     * 平台商户分账金额
     */
    public String platformAmount;
    /**
     * 分帐详细信息
     */
    public String subOrders;



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"tid\":\"")
                .append(tid).append('\"');
        sb.append(",\"msgSrc\":\"")
                .append(msgSrc).append('\"');
        sb.append(",\"requestTimestamp\":\"")
                .append(requestTimestamp).append('\"');
        sb.append(",\"merOrderId\":\"")
                .append(merOrderId).append('\"');
        sb.append(",\"mid\":\"")
                .append(mid).append('\"');
        sb.append(",\"msgType\":\"")
                .append(msgType).append('\"');
        sb.append(",\"totalAmount\":\"")
                .append(totalAmount).append('\"');
        sb.append(",\"instMid\":\"")
                .append(instMid).append('\"');
        sb.append(",\"tradeType\":\"")
                .append(tradeType).append('\"');
        sb.append(",\"sign\":\"")
                .append(sign).append('\"');
        sb.append(",\"msgId\":\"")
                .append(msgId).append('\"');
        sb.append(",\"secureTransaction\":\"")
                .append(secureTransaction).append('\"');
        sb.append(",\"srcReserve\":\"")
                .append(srcReserve).append('\"');
        sb.append(",\"subAppId\":\"")
                .append(subAppId).append('\"');
        sb.append(",\"divisionFlag\":\"")
                .append(divisionFlag).append('\"');
        sb.append(",\"platformAmount\":\"")
                .append(platformAmount).append('\"');
        sb.append(",\"subOrders\":")//sb.append(",\"subOrders\":\"")
                .append(subOrders);//.append(subOrders).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
