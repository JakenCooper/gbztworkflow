package com.gbzt.gbztworkflow.modules.taskNodePermissions.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
@Entity(name="taskNode_permissions")
public class TaskNodePermissions {
    @Id
    @Column(name = "id")
    private String id;
    //控件id
    @Column(name = "taskNodeId")
    private String taskNodeId;
    //字段名称
    @Column(name = "columnName")
    private String columnName;
    //流程id
    @Column(name = "currentFlowId")
    private String currentFlowId;
    //节点名称
    @Column(name = "taskName")
    private String taskName;
    //权限
    @Column(name = "permission")
    private String permission;
    //创建时间
    @Column(name = "create_date")
    private Date createDate;
    //删除标记
    @Column(name = "delFlag")
    private String delFlag;
    //备注
    @Column(name = "remark")
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskNodeId() {
        return taskNodeId;
    }

    public void setTaskNodeId(String taskNodeId) {
        this.taskNodeId = taskNodeId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCurrentFlowId() {
        return currentFlowId;
    }

    public void setCurrentFlowId(String currentFlowId) {
        this.currentFlowId = currentFlowId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
