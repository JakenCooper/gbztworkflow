package com.gbzt.gbztworkflow.modules.taskNodePermissions.entity;

public class AdviseTypeModel {
    private String flowId;
    private String columnName;
    private String adviseTag;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAdviseTag() {
        return adviseTag;
    }

    public void setAdviseTag(String adviseTag) {
        this.adviseTag = adviseTag;
    }
}
