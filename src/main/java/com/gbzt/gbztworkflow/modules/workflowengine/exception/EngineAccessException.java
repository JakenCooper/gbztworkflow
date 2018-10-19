package com.gbzt.gbztworkflow.modules.workflowengine.exception;

public class EngineAccessException extends EngineBaseException{
    private static final String EXCEPTION_TYPE_ACCESS= "Engine_Access";

    public EngineAccessException(String description) {
        super(EXCEPTION_TYPE_ACCESS, description);
    }

    public EngineAccessException(String exceptionType,String description) {
        super(exceptionType, description);
    }

    public EngineAccessException(String description, String message, Throwable throwable) {
        super(EXCEPTION_TYPE_ACCESS, description, message, throwable);
    }
}
