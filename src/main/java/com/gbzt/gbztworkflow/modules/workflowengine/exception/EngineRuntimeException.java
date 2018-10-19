package com.gbzt.gbztworkflow.modules.workflowengine.exception;

public class EngineRuntimeException extends EngineBaseException{
    private static final String EXCEPTION_TYPE_ACCESS= "Engine_Runtime";

    public EngineRuntimeException(String description) {
        super(EXCEPTION_TYPE_ACCESS, description);
    }

    public EngineRuntimeException(String exceptionType,String description) {
        super(exceptionType, description);
    }

    public EngineRuntimeException(String description, String message, Throwable throwable) {
        super(EXCEPTION_TYPE_ACCESS, description, message, throwable);
    }
}
