package com.runtrend.realtimecomm.netty.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;

import static com.runtrend.realtimecomm.netty.utils.ConstantUtiles.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author GanZY
 * @Title: FullHttpUtiiles
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/2216:31
 */
@Slf4j
public class FullHttpUtiles {

    public static String getContentType(HttpHeaders httpHeaders) {
        String typeStr = httpHeaders.get("Content-Type");
        String[] list = typeStr.split(";");
        return list[0];
    }

    public static FullHttpResponse stringToHttp(String content) {
        ByteBuf reply = Unpooled
                .copiedBuffer(content, CharsetUtil.UTF_8);
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, reply);
        defaultFullHttpResponse.headers()
                .setInt(CONTENT_LENGTH, reply.readableBytes());
        return defaultFullHttpResponse;

    }

    public static String sendPost(String urlParam) throws IOException {
        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(urlParam);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");

        httpClient.executeMethod(postMethod);

        String result = postMethod.getResponseBodyAsString();
        postMethod.releaseConnection();
        return result;
    }
}
