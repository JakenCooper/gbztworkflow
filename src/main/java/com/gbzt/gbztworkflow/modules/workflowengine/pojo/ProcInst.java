package com.gbzt.gbztworkflow.modules.workflowengine.pojo;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;
import com.gbzt.gbztworkflow.modules.redis.entity.RedisMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity(name="gw_run_procinst")
public class ProcInst extends BaseEntity {
    @Id
    @Column(name="id")
    @RedisMapper
    private String id;
    @Column(name="flow_id")
    @RedisMapper
    private String flowId;
    @Column(name="buss_id")
    @RedisMapper
    private String bussId;
    @Column(name="buss_table")
    @RedisMapper
    private String bussTable;

    @Column(name="finish_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    @RedisMapper
    private boolean finishTag;
    @Column(name="finish_time")
    @RedisMapper
    private Date finishTime;
    @Column(name="duration")
    @RedisMapper
    private Long duration;

    @Column(name="latest_task_id")
    @RedisMapper
    private String latestTaskId;
    @Column(name="latest_task_user")
    @RedisMapper
    private String latestTaskUser;

    @Column(name="owner_type")
    // owner range setting
    @RedisMapper
    private String ownerType;

    @Column(name="form_key")
    @RedisMapper
    private String formKey;

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

    public String getBussId() {
        return bussId;
    }

    public void setBussId(String bussId) {
        this.bussId = bussId;
    }

    public boolean isFinishTag() {
        return finishTag;
    }

    public void setFinishTag(boolean finishTag) {
        this.finishTag = finishTag;
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

    public String getLatestTaskId() {
        return latestTaskId;
    }

    public void setLatestTaskId(String latestTaskId) {
        this.latestTaskId = latestTaskId;
    }

    public String getLatestTaskUser() {
        return latestTaskUser;
    }

    public void setLatestTaskUser(String latestTaskUser) {
        this.latestTaskUser = latestTaskUser;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getBussTable() {
        return bussTable;
    }

    public void setBussTable(String bussTable) {
        this.bussTable = bussTable;
    }
}
