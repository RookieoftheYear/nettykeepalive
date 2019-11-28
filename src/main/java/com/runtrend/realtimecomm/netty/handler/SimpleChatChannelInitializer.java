package com.runtrend.realtimecomm.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author GanZY
 * @Title: SimpleChatChannelInitializer
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/209:53
 */
@Component
@RequiredArgsConstructor
public class SimpleChatChannelInitializer extends ChannelInitializer<SocketChannel> {


    private final StringEncoder ENCODER = new StringEncoder();

    private final PluginCommServiceHandler pluginCommServiceHandler;
    private final SimpleChatServerHandler simpleChatServerHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // Add the text line codec combination first,
//        pipeline.addLast(new DelimiterBasedFrameDecoder(1024 * 1024, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(new HttpRequestDecoder());
//        pipeline.addLast(ENCODER);
        pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(new HttpObjectAggregator(1024*1024));
        pipeline.addLast(pluginCommServiceHandler);
//        pipeline.addLast(simpleChatServerHandler);
    }
}
