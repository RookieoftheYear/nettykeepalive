package com.runtrend.realtimecomm.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author GanZY
 * @Title: ServerIdleStateTrigger
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2020/2/423:11
 */
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {


        if (evt instanceof IdleState) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                ctx.disconnect();
                ctx.fireChannelInactive();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
