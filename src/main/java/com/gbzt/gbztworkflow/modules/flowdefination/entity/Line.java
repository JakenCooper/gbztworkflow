package com.gbzt.gbztworkflow.modules.flowdefination.entity;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;
import com.gbzt.gbztworkflow.modules.redis.entity.RedisMapper;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity(name="gw_def_line")
public class Line extends BaseEntity {
    @Id
    @Column(name="id")
    @RedisMapper
    private String id;
    @Column(name="flow_id")
    @RedisMapper
    private String flowId;
    @Column(name="begin_node_id")
    @RedisMapper
    private String beginNodeId;
    @Column(name="end_node_id")
    @RedisMapper
    private String endNodeId;
    @Column(name="begin_node_name")
    @RedisMapper
    private String beginNodeName;
    @Column(name="end_node_name")
    @RedisMapper
    private String endNodeName;

    @Column(name="can_withdraw")
    @org.hibernate.annotations.Type(type="yes_no")
    @RedisMapper
    private Boolean canWithdraw;
    @Column(name="can_retreat")
    @org.hibernate.annotations.Type(type="yes_no")
    @RedisMapper
    private Boolean canRetreat;
    @Column(name="task_type")
    @RedisMapper
    private String taskType;
    @Column(name="finish_type")
    @RedisMapper
    private String finishType;
    @Column(name="exec_type")
    @RedisMapper
    private String execType;

    @Transient
    private String sysTaskName;

    public Line() {
    }

    public Line(String beginNodeId, String endNodeId) {
        this.beginNodeId = beginNodeId;
        this.endNodeId = endNodeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getBeginNodeId() {
        return beginNodeId;
    }

    public void setBeginNodeId(String beginNodeId) {
        this.beginNodeId = beginNodeId;
    }

    public String getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(String endNodeId) {
        this.endNodeId = endNodeId;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public Boolean getCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(Boolean canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

    public Boolean getCanRetreat() {
        return canRetreat;
    }

    public void setCanRetreat(Boolean canRetreat) {
        this.canRetreat = canRetreat;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public String getSysTaskName() {
        return sysTaskName;
    }

    public void setSysTaskName(String sysTaskName) {
        this.sysTaskName = sysTaskName;
    }

}
