package com.runtrend.realtimecomm.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author GanZY
 * @Title: SimpleChatChannelInitializer
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/209:53
 */
@Component
@RequiredArgsConstructor
public class PluginsCommServiceInitializer extends ChannelInitializer<SocketChannel> {


    private final StringEncoder ENCODER = new StringEncoder();

    private final PluginCommServiceHandler pluginCommServiceHandler;
    private final SimpleChatServerHandler simpleChatServerHandler;


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new IdleStateHandlerWrapper(50, 0, 0, TimeUnit.SECONDS));
//        pipeline.addLast(new ServerIdleStateTrigger());
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(new HttpObjectAggregator(1024*100));
        pipeline.addLast(new ServerIdleStateTrigger());
        pipeline.addLast(pluginCommServiceHandler);

    }
}
