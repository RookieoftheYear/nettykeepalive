package com.runtrend.realtimecomm.netty.utils;

import io.netty.util.AsciiString;

/**
 * @author GanZY
 * @Title: ConstantUtiles
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/2216:35
 */
public class ConstantUtiles {

    public static final String FAVICON_ICO = "/favicon.ico";
    public static final String GATEWAY_CONNECTION = "/gatewayconnection";
    public static final String GATEWAY_QUERY = "/query";
    public static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    public static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    public static final AsciiString CONNECTION = AsciiString.cached("Connection");
    public static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");
    public static final String POST_URL = "http://plugins.awifi.cn/pluginsmanage/open/sendMsg.api?mobile={0}&content={1}";
    public static final String REGISTER_MSG = "您办理的空中卫士宽带版业务（{0}）已经激活。";
    public static final String GATEWAY_FLAG = "1";
    public static final String NODE_FLAG = "0";
}
