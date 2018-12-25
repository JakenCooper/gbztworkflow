package com.gbzt.gbztworkflow.modules.flowruntime.model;

import java.io.Serializable;
import java.util.Date;
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
    private String nodeName;
    private String taskType;

    private String taskId;
    private String description;
    private boolean completeAtOnce;

    private String executionType;
    private String executionOrder;

    public String retreatOperType;
    public boolean retreatSubmitTag;
    public boolean retreatOperPermission;

    /*事务查询属性*/
    private String chColName;//label名称
    private String colName;//字段名称
    private String searchType;//查询类型
    /*end*/
    /*文件上传属性:*/
    private String fileName;
    private String fileRealUrl;
    private String fileUrl;
    private String procInsId;
    private Date createDate;
    private String uploadBy;
    private String delFlag;
    /*end*/
    //查询开始节点名称
    private String startNodesName;
    /* 是否为暂存 start*/
    private boolean tempSaveTag = false;
    /* 是否为暂存 end*/
	
    public boolean isTempSaveTag() {
        return tempSaveTag;
    }

    public void setTempSaveTag(boolean tempSaveTag) {
        this.tempSaveTag = tempSaveTag;
    }

    public String getStartNodesName() {
        return startNodesName;
    }

    public void setStartNodesName(String startNodesName) {
        this.startNodesName = startNodesName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileRealUrl() {
        return fileRealUrl;
    }

    public void setFileRealUrl(String fileRealUrl) {
        this.fileRealUrl = fileRealUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }



    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getChColName() {
        return chColName;
    }

    public void setChColName(String chColName) {
        this.chColName = chColName;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getRetreatOperType() {
        return retreatOperType;
    }

    public void setRetreatOperType(String retreatOperType) {
        this.retreatOperType = retreatOperType;
    }

    public boolean isRetreatSubmitTag() {
        return retreatSubmitTag;
    }

    public void setRetreatSubmitTag(boolean retreatSubmitTag) {
        this.retreatSubmitTag = retreatSubmitTag;
    }

    public boolean isRetreatOperPermission() {
        return retreatOperPermission;
    }

    public void setRetreatOperPermission(boolean retreatOperPermission) {
        this.retreatOperPermission = retreatOperPermission;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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