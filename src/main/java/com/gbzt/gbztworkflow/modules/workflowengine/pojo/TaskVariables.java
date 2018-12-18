package com.gbzt.gbztworkflow.modules.workflowengine.pojo;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;

import javax.persistence.*;

@Entity(name="gw_run_variables")
public class TaskVariables extends BaseEntity {
    public static final String VARS_TYPE_PROC_PREFIX = "proc_";
    public static final String VARS_TYPE_TASK_PREFIX = "task_";


    public static final String VARS_TYPE_PROC = "proc";
    public static final String VARS_TYPE_TASK = "task";

    @Id
    @Column(name="id")
    private String id;
    @Column(name="node_id")
    private String nodeId;
    @Column(name="task_id")
    private String taskId;
    @Column(name="proc_inst_id")
    private String procInstId;
    @Column(name="var_key")
    private String key;
    @Column(name="var_value")
    private String value;
    @Column(name="var_type")
    private String type;

//    @ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="task_id")
//    private Task task;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
