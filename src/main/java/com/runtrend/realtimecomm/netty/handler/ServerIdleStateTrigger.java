package com.runtrend.realtimecomm.netty.handler;

import com.runtrend.realtimecomm.netty.utils.FullHttpUtiles;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GanZY
 * @Title: ServerIdleStateTrigger
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2020/2/423:11
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {


        if (evt instanceof IdleState) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                ctx.writeAndFlush(FullHttpUtiles.sendHeartbeat(""))
                        .addListener(future -> {
                            if (future.isSuccess()) {
                                log.debug("=== Send Heartbeat to {} Successfully",ctx.channel().remoteAddress());
                            }
                        });

//                ctx.fireChannelInactive();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
