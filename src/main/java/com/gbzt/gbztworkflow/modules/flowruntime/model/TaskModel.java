package com.gbzt.gbztworkflow.modules.flowruntime.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskModel extends BaseModel{

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
}
