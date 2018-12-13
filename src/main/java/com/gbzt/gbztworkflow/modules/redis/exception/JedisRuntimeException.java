package com.gbzt.gbztworkflow.modules.redis.exception;

public class JedisRuntimeException extends RuntimeException {
    private String message;
    public JedisRuntimeException(String message) {
        super(message);
    }
    public JedisRuntimeException(String message,Throwable e) {
        super(message,e);
    }
    public JedisRuntimeException(Throwable e) {
        super(e.getMessage(),e);
    }
}
