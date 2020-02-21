package com.runtrend.realtimecomm.netty.handler;

import com.runtrend.realtimecomm.netty.ChannelRepository;
import com.runtrend.realtimecomm.netty.utils.ConstantUtiles;
import com.runtrend.realtimecomm.netty.utils.FullHttpUtiles;
import com.runtrend.realtimecomm.serivce.AccountService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Optional;

/**
 * @author GanZY
 * @Title: PluginCommServiceHandler
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/2121:50
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class PluginCommServiceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private final AttributeKey<String> attributeKey = AttributeKey.valueOf("MAC");
    private final ChannelRepository channelRepository;
    private final AccountService accountService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        ctx.fireChannelActive();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {

        String header = fullHttpRequest.headers().toString();
        log.debug("==== Message in Read0, Header is {} ===",header);
        URI uri = new URI(fullHttpRequest.uri());
        String path = uri.getPath();
        if (ConstantUtiles.FAVICON_ICO.equals(path)) {
            return;
        }


        Optional.of(fullHttpRequest.method())
                .filter(httpMethod -> httpMethod.equals(HttpMethod.GET))
                .map(x -> queryProcess(ctx, uri))
                .orElseGet(() -> communicationProcess(ctx, fullHttpRequest));
    }



    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        Assert.notNull(ctx, "[Assertion failed] - ChannelHandlerContext is required; it must not be null");
        this.channelRepository.remove(ctx.channel().attr(attributeKey).get());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }

    // Deal with API:
    private String queryProcess(ChannelHandlerContext ctx, URI uri) {

        String path = uri.getPath();
        StringBuffer resReply = new StringBuffer();
        if (ConstantUtiles.GATEWAY_CONNECTION.equals(path)) {


            FullHttpResponse response = FullHttpUtiles.stringToHttp(this.channelRepository.getChannels().toString());
            ctx.writeAndFlush(response).addListeners(ChannelFutureListener.CLOSE,
                    (ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            resReply.append("Response to Query Successfully");
                        } else {
                            String message = future.cause().getMessage();
                            resReply.append("Response to Query Fail : ").append(message);
                        }
                        log.debug("Response to {}: {}", path, resReply);
                    }
            );

        } else if (ConstantUtiles.GATEWAY_QUERY.equals(path)) {
            String mac = uri.getQuery().split("=")[1];
            Boolean isActive = Optional.ofNullable(this.channelRepository.get(mac))
                    .map(Channel::isActive)
                    .orElse(false);
            FullHttpResponse reply = FullHttpUtiles.stringToHttp(String.valueOf(isActive));
            ctx.writeAndFlush(reply).addListeners(ChannelFutureListener.CLOSE,
                    (ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            resReply.append("Response to Query Successfully");
                        } else {
                            String message = future.cause().getMessage();
                            resReply.append("Response to Query Fail :").append(message);
                        }
                        log.debug("Response to {}: {}", path, resReply);
                    });

        }


        return resReply.toString();
    }

    // Main Process
    private String communicationProcess(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {

        StringBuffer reply = new StringBuffer();
        String mac = fullHttpRequest.headers().get("macID");

        Assert.notNull(mac, "[Assertion failed] - mac is required; it must not be null");
        String opType = fullHttpRequest.headers().get("OpType");
        ByteBuf content = fullHttpRequest.content();
        content.retain();
        Optional.ofNullable(fullHttpRequest.headers().get("isGateway"))
                .filter(ConstantUtiles.GATEWAY_FLAG::equals)
                .map(x -> {
                    Optional.ofNullable(channelRepository.get(mac))
                            .map(y -> {
                                //在连接池里，通道active且是心跳操作
                                if (y.isActive() && ConstantUtiles.HEARTBEAT_OP.equals(opType)) {
                                    log.debug("=== {} Registered Before and Still Alive ===", mac);
                                    reply.append(MessageFormat.format("=== {0}  Registered Before ===", mac));
                                } else {
                                    //在连接池里，通道inactive或者是注册操作
                                    log.debug("=== {} is Updating: The Operation Type : {} , Alive Status : {} ===", mac,opType,y.isActive());
                                    y.close().addListener(future -> {

                                        if (future.isSuccess()) {
                                            ctx.channel().attr(attributeKey).setIfAbsent(mac);
                                            channelRepository.put(mac, ctx.channel());
                                            log.debug("=== {} Update Successfully ===", mac);
                                            reply.append(MessageFormat.format("=== {0}  Update Successfully ===", mac));
                                        } else {
                                            String message = future.cause().getMessage();
                                            log.debug("=== {} Update Fail ===", mac);
                                            reply.append(MessageFormat.format("=== {0}  Update Fail :", mac))
                                                    .append(message).append(" ===");
                                        }
                                    });

                                }

                                return reply;
                            })
                            .orElseGet(() -> {

                                ctx.channel().attr(attributeKey).setIfAbsent(mac);
                                channelRepository.put(mac, ctx.channel());
                                log.debug("=== {} Register Successfully ===", mac);
                                accountService.sendMessage(mac);
                                return reply.append(MessageFormat.format("=== {0} Registered Successfully ===", mac));
                            });

                    ctx.writeAndFlush(FullHttpUtiles.stringToHttp(reply.toString()));
                    return reply;
                })
                .orElseGet(() -> {
                    Optional.ofNullable(channelRepository.get(mac))
                            .map(channel -> {
                                channel.writeAndFlush(FullHttpUtiles.stringToHttp(content.toString(CharsetUtil.UTF_8)))
                                        .addListener((ChannelFutureListener) future -> {
                                            if (future.isSuccess()) {
                                                log.debug("=== Send Message to {} Successfully ===", mac);
                                                reply.append(MessageFormat.format("=== Send Message to {0} Successfully ===", mac));

                                            } else {
                                                log.debug("=== Send Message to {} Fail:{}", mac, future.cause());
                                                reply.append(MessageFormat.format("=== Send Message to {0} Fail:{1}", mac, future.cause()));
                                            }
                                        });

                                return reply;
                            })
                            .orElseGet(() -> {
                                log.debug("=== Target Gateway {} Do Not Exist === ", mac);

                                reply.append(MessageFormat.format("=== Target Gateway {0} Do Not Exist === ", mac));


                                return reply;
                            });
                    ctx.writeAndFlush(FullHttpUtiles.stringToHttp(reply.toString())).addListener(ChannelFutureListener.CLOSE);
                    return reply;
                });
        return reply.toString();
    }

}
