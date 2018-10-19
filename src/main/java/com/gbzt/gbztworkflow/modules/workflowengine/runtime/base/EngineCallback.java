package com.gbzt.gbztworkflow.modules.workflowengine.runtime.base;


import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;

public interface EngineCallback {

    public <T> T handleCallback(EngineTask task) throws EngineRuntimeException;

}
