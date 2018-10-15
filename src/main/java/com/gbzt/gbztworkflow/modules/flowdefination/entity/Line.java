package com.gbzt.gbztworkflow.modules.flowdefination.entity;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;
import com.sun.xml.internal.rngom.parse.host.Base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="gw_def_line")
public class Line extends BaseEntity {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="flow_id")
    private String flowId;
    @Column(name="begin_node_id")
    private String beginNodeId;
    @Column(name="end_node_id")
    private String endNodeId;
    @Column(name="begin_node_name")
    private String beginNodeName;
    @Column(name="end_node_name")
    private String endNodeName;

    @Column(name="can_withdraw")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean canWithdraw;
    @Column(name="can_retreat")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean canRetreat;
    @Column(name="is_exec_single")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean execSingle = true;

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

    public boolean isCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(boolean canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

    public boolean isCanRetreat() {
        return canRetreat;
    }

    public void setCanRetreat(boolean canRetreat) {
        this.canRetreat = canRetreat;
    }

    public boolean isExecSingle() {
        return execSingle;
    }

    public void setExecSingle(boolean execSingle) {
        this.execSingle = execSingle;
    }
}
