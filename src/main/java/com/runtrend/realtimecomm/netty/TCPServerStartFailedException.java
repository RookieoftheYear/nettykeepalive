package com.runtrend.realtimecomm.netty;

/**
 * @author GanZY
 * @Title: TCPServerStartFailedException
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/209:58
 */
public class TCPServerStartFailedException  extends RuntimeException {

    public TCPServerStartFailedException() {
    }

    public TCPServerStartFailedException(String message) {
        super(message);
    }

    public TCPServerStartFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TCPServerStartFailedException(Throwable cause) {
        super(cause);
    }

    public TCPServerStartFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
