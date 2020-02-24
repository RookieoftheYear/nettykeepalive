package com.runtrend.realtimecomm.netty;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author GanZY
 * @Title: ChannelRepository
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/209:56
 */
public class ChannelRepository {

    private  ConcurrentMap<String, Channel> channelCache = new ConcurrentHashMap<>();

    public ChannelRepository put(String key, Channel value) {
        this.channelCache.put(key, value);
        return this;
    }

    public Channel get(String key) {
        return channelCache.get(key);
    }
    //修改
    public void remove(String key) {
        this.channelCache.remove(key);
    }

    public boolean remove(String key, Channel val) {
        if (key.equals(""))
            return false;

        return this.channelCache.remove(key, val);
    }

    public int size() {
        return channelCache.size();
    }

    public StringBuffer getChannels() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(size()).append("\n");
        this.channelCache.forEach((key, value) ->
                stringBuffer.append(key).append("-").append(value.remoteAddress()).append("\n"));
        return stringBuffer;

    }


}
