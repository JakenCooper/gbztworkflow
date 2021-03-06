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
    private String fileId;
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
    private boolean tempSaveTag = false;//是否为暂存
    private Boolean transferOut; //判断当前节点是否可转出
    private long count;// 总记录数，设置为“-1”表示不查询总数

    private String orderBy;//排序字段
    private String searchAll="no";

    private String endPassNum;//结束节点序号

    private boolean canEnd;//判断当前节点是否有结束连线

    private String begainLineId;//连线开始id

    private String endLineId;//连线终止id


    private String adviseBussTableName; //意见业务表名称

    private String secondLastTaskId; //倒数第二步任务id

    public String getSecondLastTaskId() {
        return secondLastTaskId;
    }

    public void setSecondLastTaskId(String secondLastTaskId) {
        this.secondLastTaskId = secondLastTaskId;
    }

    public String getAdviseBussTableName() {
        return adviseBussTableName;
    }

    public void setAdviseBussTableName(String adviseBussTableName) {
        this.adviseBussTableName = adviseBussTableName;
    }

    public String getBegainLineId() {
        return begainLineId;
    }

    public void setBegainLineId(String begainLineId) {
        this.begainLineId = begainLineId;
    }

    public String getEndLineId() {
        return endLineId;
    }

    public void setEndLineId(String endLineId) {
        this.endLineId = endLineId;
    }

    public boolean isCanEnd() {
        return canEnd;
    }

    public void setCanEnd(boolean canEnd) {
        this.canEnd = canEnd;
    }

    public String getEndPassNum() {
        return endPassNum;
    }

    public void setEndPassNum(String endPassNum) {
        this.endPassNum = endPassNum;
    }

    public String getSearchAll() {
        return searchAll;
    }

    public void setSearchAll(String searchAll) {
        this.searchAll = searchAll;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    private String ownerUser;
	
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

    public String getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(String ownerUser) {
        this.ownerUser = ownerUser;
    }


    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Boolean getTransferOut() {
        return transferOut;
    }

    public void setTransferOut(Boolean transferOut) {
        this.transferOut = transferOut;
    }
}