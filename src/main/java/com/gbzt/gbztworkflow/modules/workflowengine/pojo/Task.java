package com.gbzt.gbztworkflow.modules.workflowengine.pojo;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Entity(name="gw_run_task")
public class Task extends BaseEntity {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="task_type")
    private String taskType;  //usertask or systask
    @Column(name="proc_inst_id")
    private String procInstId;
    @Column(name="flow_id")
    private String flowId;
    @Column(name="buss_id")
    private String bussId;
    @Column(name="buss_table")
    private String bussTable;
    @Column(name="node_id")
    private String nodeId;
    @Column(name="node_name")
    private String nodeName;
    @Column(name="node_def_id")
    private String nodeDefId;

    @Column(name="parent_task_id")
    private String parentTaskId;
    @Column(name="child_task_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean childTaskTag;
    @Transient
    private Task parentTask;
    @Transient
    private List<Task> subTasks;

    @Column(name="finish_type")
    private String finishType; // af,due to pass & [model]
    @Column(name="assign_user")
    private String assignUser; // nf (atten:diff from assignuser in tasks and execs: user in next step or current step)
    @Column(name="claim_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean claimTag; // nf(sub) and af due to claim
    @Column(name="claim_user")
    private String claimUser; // nf(sub) and af due to claim
    @Transient
    private List<String> assignUserList; // af
    @Column(name="owner")
    private String owner; // af,due to finishuser and owner settings [model]
    @Column(name="assign_time")
    private Date assignTime; //nf
    @Column(name="claim_time")
    private Date claimTime; //nf
    @Column(name="pass_str")
    private String passStr; //af

    @Column(name="finish_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean finishTag;
    @Column(name="finish_user")
    private String finishUser;
    @Column(name="finish_time")
    private Date finishTime;
    @Column(name="duration")
    private Long duration;
    @Column(name="description")
    private String description;
    @Column(name="withdraw_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean withdrawTag;
    @Column(name="retreat_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean retreatTag;
    @Column(name="withdraw_description")
    private String withdrawDescription;
    @Column(name="retreat_description")
    private String retreatDescription;

    @Column(name="create_time_mills")
    private Long createTimeMills;

    // only useful for "multi" finish type
    @Column(name="execution_type")
    private String executionType; // (block or concurrent) [model]
    @Column(name="execution_order")
    private String executionOrder; // (if block,order for execution or decide every step)

    @Transient
    private boolean qcTag;
    @Transient
    private String qcUser;
    @Transient
    private boolean qcRtnTag;
    @Transient
    private String qcRtnDescription;

    @Transient
    private boolean transTag;
    @Transient
    private String transUser;
    @Transient
    private String transDescription;

    @Column(name="priority")
    private Integer priority=1;
    @Column(name="emergency_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean emergencyTag;

    @Column(name="lock_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean lock;


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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
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

    public String getNodeDefId() {
        return nodeDefId;
    }

    public void setNodeDefId(String nodeDefId) {
        this.nodeDefId = nodeDefId;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    public String getAssignUser() {
        return assignUser;
    }

    public void setAssignUser(String assignUser) {
        this.assignUser = assignUser;
    }

    public boolean isClaimTag() {
        return claimTag;
    }

    public void setClaimTag(boolean claimTag) {
        this.claimTag = claimTag;
    }

    public String getClaimUser() {
        return claimUser;
    }

    public void setClaimUser(String claimUser) {
        this.claimUser = claimUser;
    }

    public List<String> getAssignUserList() {
        return assignUserList;
    }

    public void setAssignUserList(List<String> assignUserList) {
        this.assignUserList = assignUserList;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(Date assignTime) {
        this.assignTime = assignTime;
    }

    public Date getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(Date claimTime) {
        this.claimTime = claimTime;
    }

    public String getPassStr() {
        return passStr;
    }

    public void setPassStr(String passStr) {
        this.passStr = passStr;
    }

    public boolean isFinishTag() {
        return finishTag;
    }

    public void setFinishTag(boolean finishTag) {
        this.finishTag = finishTag;
    }

    public String getFinishUser() {
        return finishUser;
    }

    public void setFinishUser(String finishUser) {
        this.finishUser = finishUser;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWithdrawTag() {
        return withdrawTag;
    }

    public void setWithdrawTag(boolean withdrawTag) {
        this.withdrawTag = withdrawTag;
    }

    public boolean isRetreatTag() {
        return retreatTag;
    }

    public void setRetreatTag(boolean retreatTag) {
        this.retreatTag = retreatTag;
    }

    public String getWithdrawDescription() {
        return withdrawDescription;
    }

    public void setWithdrawDescription(String withdrawDescription) {
        this.withdrawDescription = withdrawDescription;
    }

    public String getRetreatDescription() {
        return retreatDescription;
    }

    public void setRetreatDescription(String retreatDescription) {
        this.retreatDescription = retreatDescription;
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

    public boolean isQcTag() {
        return qcTag;
    }

    public void setQcTag(boolean qcTag) {
        this.qcTag = qcTag;
    }

    public String getQcUser() {
        return qcUser;
    }

    public void setQcUser(String qcUser) {
        this.qcUser = qcUser;
    }

    public boolean isQcRtnTag() {
        return qcRtnTag;
    }

    public void setQcRtnTag(boolean qcRtnTag) {
        this.qcRtnTag = qcRtnTag;
    }

    public String getQcRtnDescription() {
        return qcRtnDescription;
    }

    public void setQcRtnDescription(String qcRtnDescription) {
        this.qcRtnDescription = qcRtnDescription;
    }

    public boolean isTransTag() {
        return transTag;
    }

    public void setTransTag(boolean transTag) {
        this.transTag = transTag;
    }

    public String getTransUser() {
        return transUser;
    }

    public void setTransUser(String transUser) {
        this.transUser = transUser;
    }

    public String getTransDescription() {
        return transDescription;
    }

    public void setTransDescription(String transDescription) {
        this.transDescription = transDescription;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean isEmergencyTag() {
        return emergencyTag;
    }

    public void setEmergencyTag(boolean emergencyTag) {
        this.emergencyTag = emergencyTag;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public boolean isChildTaskTag() {
        return childTaskTag;
    }

    public void setChildTaskTag(boolean childTaskTag) {
        this.childTaskTag = childTaskTag;
    }

    public String getBussId() {
        return bussId;
    }

    public void setBussId(String bussId) {
        this.bussId = bussId;
    }

    public String getBussTable() {
        return bussTable;
    }

    public void setBussTable(String bussTable) {
        this.bussTable = bussTable;
    }
}
