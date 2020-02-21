package com.runtrend.realtimecomm.netty.handler;

import com.runtrend.realtimecomm.netty.utils.FullHttpUtiles;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author GanZY
 * @Title: ServerIdleStateTrigger
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2020/2/423:11
 */
@Slf4j

public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {


    private final AttributeKey<String> attributeKey = AttributeKey.valueOf("MAC");
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        Optional.ofNullable(ctx.channel().attr(attributeKey).get())
                .map(x -> {
                    if (evt instanceof IdleStateEvent) {
                        IdleState state = ((IdleStateEvent) evt).state();
                        if (state == IdleState.READER_IDLE) {
                            log.debug("=== READER_IDLE Invoked ===");
                            ctx.writeAndFlush(FullHttpUtiles.sendHeartbeat(""))
                                    .addListener(future -> {
                                        if (future.isSuccess()) {
                                            log.debug("=== Send Heartbeat to {} Successfully", ctx.channel().remoteAddress());
                                        } else {
                                            log.debug("=== Send Heartbeat to {} Fail for {} ===", ctx.channel().remoteAddress(), future.cause().getMessage());
                                        }
                                    });

                        }

                    } else {
                        try {
                            super.userEventTriggered(ctx, evt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return x;
                })
                .orElseGet(() -> {
                    log.debug("=== This Channel is Not a Gateway ===");
                    return "";
                });

    }

}
