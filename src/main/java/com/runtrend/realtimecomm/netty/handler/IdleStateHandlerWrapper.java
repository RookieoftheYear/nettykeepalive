package com.runtrend.realtimecomm.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;
import io.netty.util.AttributeKey;
import io.netty.util.Attribute;


public class IdleStateHandlerWrapper extends IdleStateHandler {
    private final AttributeKey<Integer> attributeKey = AttributeKey.valueOf("TimeoutCount");


    public IdleStateHandlerWrapper(
            int readerIdleTimeSeconds,
            int writerIdleTimeSeconds,
            int allIdleTimeSeconds) {

        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    public IdleStateHandlerWrapper(
            long readerIdleTime, long writerIdleTime, long allIdleTime,
            TimeUnit unit) {
        super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }


    public IdleStateHandlerWrapper(boolean observeOutput,
            long readerIdleTime, long writerIdleTime, long allIdleTime,
            TimeUnit unit) {
    	super(observeOutput,
            readerIdleTime, writerIdleTime, allIdleTime,
            unit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Attribute<Integer> timeoutCount = ctx.channel().attr(attributeKey);
        timeoutCount.set(0);

        super.channelRead(ctx, msg);
    }
}

