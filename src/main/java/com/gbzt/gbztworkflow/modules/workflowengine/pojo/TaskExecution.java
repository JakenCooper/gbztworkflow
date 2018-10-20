package com.gbzt.gbztworkflow.modules.workflowengine.pojo;

import java.util.List;

public class TaskExecution {
    public String id;
    public String flowName;
    //public String taskId;

    public String flowId;
    public String bussId;
    public String formKey;
    public String passStr;
    public String passUser; // -- common


    public String procInstId;
    public String nodeId;
    public String nodeDefId;
    public String assignUser;
    public List<String> assignUserList;
    public String taskType;



    public String taskId;
    public String description;
    // public String passStr;
    // public String assignUser;
    // public String assignUserList;
    /**special argument*/
    public boolean completeAtOnce;


    //public String passUser;
    public Integer pageNum;
    public Integer pageSize;
    public Integer totalPage;


    public boolean claimTag;

    public String executionType;
    public String executionOrder;

    public String oper;
    public String runableType;



    public String getBussId() {
        return bussId;
    }

    public void setBussId(String bussId) {
        this.bussId = bussId;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPassUser() {
        return passUser;
    }

    public void setPassUser(String passUser) {
        this.passUser = passUser;
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

    public boolean isClaimTag() {
        return claimTag;
    }

    public void setClaimTag(boolean claimTag) {
        this.claimTag = claimTag;
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

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getRunableType() {
        return runableType;
    }

    public void setRunableType(String runableType) {
        this.runableType = runableType;
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

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
