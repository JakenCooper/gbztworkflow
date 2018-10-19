package com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity;

import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineCallback;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineExecutable;

import java.util.ArrayList;
import java.util.List;

public class EngineTask {

    private String taskId;
    private String taskType;
    private String taskName;
    private String executionType;
    private Class<? extends EngineExecutable> executor;
    private Class<? extends EngineCallback> callback;
    private Object args;
    private Object executedResult;

    private String threadType;
    private Integer currentIndex;
    private List<EngineTask> children = new ArrayList<EngineTask>();
    private EngineTask parent;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public Class<? extends EngineExecutable> getExecutor() {
        return executor;
    }

    public void setExecutor(Class<? extends EngineExecutable> executor) {
        this.executor = executor;
    }

    public Class<? extends EngineCallback> getCallback() {
        return callback;
    }

    public void setCallback(Class<? extends EngineCallback> callback) {
        this.callback = callback;
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }

    public Object getExecutedResult() {
        return executedResult;
    }

    public void setExecutedResult(Object executedResult) {
        this.executedResult = executedResult;
    }

    public String getThreadType() {
        return threadType;
    }

    public void setThreadType(String threadType) {
        this.threadType = threadType;
    }

    public Integer getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(Integer currentIndex) {
        this.currentIndex = currentIndex;
    }

    public List<EngineTask> getChildren() {
        return children;
    }

    public void setChildren(List<EngineTask> children) {
        this.children = children;
    }

    public EngineTask getParent() {
        return parent;
    }

    public void setParent(EngineTask parent) {
        this.parent = parent;
    }
}
