package com.gbzt.gbztworkflow.modules.flowruntime.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskModel extends BaseModel implements Serializable {

    private static final long serialVersionUID = -1136458177034784424L;

    private static final Set<String> allowdOperSet = new HashSet<String>();

    static{
        allowdOperSet.add("fetchNextStep");
    }

    private String flowId;
    private String flowName;

    private String passStr;
    private String assignUser;
    private List<String> assignUserList;

    private String procInstId;
    private String nodeId;
    private String nodeDefId;
    private String taskType;

    private String taskId;
    private String description;
    private boolean completeAtOnce;

    private String executionType;
    private String executionOrder;


    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getPassStr() {
        return passStr;
    }

    public void setPassStr(String passStr) {
        this.passStr = passStr;
    }

    public String getAssignUser() {
        return assignUser;
    }

    public void setAssignUser(String assignUser) {
        this.assignUser = assignUser;
    }

    public List<String> getAssignUserList() {
        return assignUserList;
    }

    public void setAssignUserList(List<String> assignUserList) {
        this.assignUserList = assignUserList;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeDefId() {
        return nodeDefId;
    }

    public void setNodeDefId(String nodeDefId) {
        this.nodeDefId = nodeDefId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleteAtOnce() {
        return completeAtOnce;
    }

    public void setCompleteAtOnce(boolean completeAtOnce) {
        this.completeAtOnce = completeAtOnce;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public String getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(String executionOrder) {
        this.executionOrder = executionOrder;
    }
}
