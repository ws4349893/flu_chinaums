package com.titilife.flu_chinaums.model;

/**
 * Created by xiulu on 2017/5/24.
 */
public class PostonRequest {
    public String msgId;
    public String msgSrc;
    public String msgType;
    public String requestTimestamp;
    public String merOrderId;
    public String mid;
    public String tid;
    /**
     * 机构商户号
     */
    public String instMid;
    public String totalAmount;
    public String merchantUserId;
    public String mobile;
    public String orderSource;
    public String sign;
    public String secureTransaction;
    /**
     * 商户想定制化展示的内容，长度不大于255
     */
    public String srcReserve;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"msgId\":\"")
                .append(msgId).append('\"');
        sb.append(",\"msgSrc\":\"")
                .append(msgSrc).append('\"');
        sb.append(",\"msgType\":\"")
                .append(msgType).append('\"');
        sb.append(",\"requestTimestamp\":\"")
                .append(requestTimestamp).append('\"');
        sb.append(",\"merOrderId\":\"")
                .append(merOrderId).append('\"');
        sb.append(",\"mid\":\"")
                .append(mid).append('\"');
        sb.append(",\"tid\":\"")
                .append(tid).append('\"');
        sb.append(",\"instMid\":\"")
                .append(instMid).append('\"');
        sb.append(",\"totalAmount\":\"")
                .append(totalAmount).append('\"');
        sb.append(",\"merchantUserId\":\"")
                .append(merchantUserId).append('\"');
        sb.append(",\"mobile\":\"")
                .append(mobile).append('\"');
        sb.append(",\"orderSource\":\"")
                .append(orderSource).append('\"');
        sb.append(",\"sign\":\"")
                .append(sign).append('\"');
        sb.append(",\"secureTransaction\":\"")
                .append(secureTransaction).append('\"');
        sb.append(",\"srcReserve\":\"")
                .append(srcReserve).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
