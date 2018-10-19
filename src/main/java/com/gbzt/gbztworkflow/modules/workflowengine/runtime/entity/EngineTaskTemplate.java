package com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity;

import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineCallback;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineExecutable;

public class EngineTaskTemplate extends EngineTask {
    private String templateName;

    public EngineTaskTemplate(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


    public void fullfillTemplateInfo(String taskName,String executionType,String threadType,Class<? extends EngineExecutable> executor,
                                     Class<? extends EngineCallback> callback){
        setTaskName(taskName);
        setExecutionType(executionType);
        setThreadType(threadType);
        setExecutor(executor);
        setCallback(callback);
    }
}
