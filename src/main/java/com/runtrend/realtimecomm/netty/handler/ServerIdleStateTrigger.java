package com.runtrend.realtimecomm.netty.handler;

import com.runtrend.realtimecomm.netty.utils.FullHttpUtiles;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
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
	private final AttributeKey<Integer> timeoutAttrKey = AttributeKey.valueOf("TimeoutCount");

    private final AttributeKey<String> attributeKey = AttributeKey.valueOf("MAC");
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {


	if (evt instanceof IdleStateEvent) {
		IdleState state = ((IdleStateEvent) evt).state();
		if (state == IdleState.READER_IDLE) {
			// log.debug("=== READER_IDLE Invoked ===");
			Optional.ofNullable(ctx.channel().attr(attributeKey).get())
            .map(x -> {
				Attribute<Integer> timeoutCount = ctx.channel().attr(timeoutAttrKey);
				if (timeoutCount.get() == null) {
					timeoutCount.set(0);
				} else {
					Integer cnt = timeoutCount.get();
					if (cnt >= 3) {
						// close channel
						ctx.channel().close();
						log.debug("=== timeoutCount is {}, closing channel of mac {}, IP {} ===", cnt, x, ctx.channel().remoteAddress());
						return x;
					} else	timeoutCount.set(++cnt);
					// log.debug("=== timeoutCount of mac {}, IP {} is set to {} ===", x, ctx.channel().remoteAddress(), cnt);
				}

				// ctx.writeAndFlush(FullHttpUtiles.sendHeartbeat("")) // will have nothing replied
				// ctx.writeAndFlush(FullHttpUtiles.stringToHttp("405")) // will have 405OK replied, plugin may crush
				ctx.writeAndFlush(FullHttpUtiles.stringToHttp("{}"))  // will have 200OK replied
				.addListener(future -> {
				    if (future.isSuccess()) {
						// log.debug("=== Send Heartbeat to mac {} of IP {} Successfully", x, ctx.channel().remoteAddress());
                    } else {
                     	log.debug("=== Send Heartbeat to mac {} of IP {} Fail for {} ===", x, ctx.channel().remoteAddress(), future.cause().getMessage());
				    }
				});
				return x;
			})
			.orElseGet(() -> {
				log.debug("=== This Channel is Not a Gateway, {} ===", ctx.channel().remoteAddress());
				// ctx.fireChannelInactive(); fireChannelInactive will not really close channel, use close instead.
				ctx.channel().close();
				return "";
			});
		} else if (state == IdleState.WRITER_IDLE) {
				log.debug("=== WRITER_IDLE Invoked ===");
		} else if (state == IdleState.ALL_IDLE) {
				log.debug("=== ALL_IDLE Invoked ===");
		}
		return;
	} 
		
	try {
		super.userEventTriggered(ctx, evt);
	} catch (Exception e) {
		e.printStackTrace();
	}

    }	

}

