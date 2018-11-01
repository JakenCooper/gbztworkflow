package com.gbzt.gbztworkflow.modules.workflowengine.pojo;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="gw_run_histproc")
public class HistProc extends BaseEntity {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="task_id")
    private String taskId;
    @Column(name="proc_inst_id")
    private String procInstId;
    @Column(name="user_id")
    private String userId;
    @Column(name="node_id")
    private String nodeId;
    @Column(name="node_name")
    private String nodeName;

    @Column(name="create_time_mills")
    private Long createTimeMills;

    public Long getCreateTimeMills() {
        return createTimeMills;
    }

    public void setCreateTimeMills(Long createTimeMills) {
        this.createTimeMills = createTimeMills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
