package com.gbzt.gbztworkflow.modules.workflowengine.runtime.base;

import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;

public interface EngineExecutable {

    public <T> T executeEngineTask(EngineTask task) throws EngineRuntimeException;

    public void preHandleTask(EngineTask task) throws EngineAccessException;
}
