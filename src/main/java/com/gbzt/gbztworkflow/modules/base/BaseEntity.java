package com.gbzt.gbztworkflow.modules.base;

import com.gbzt.gbztworkflow.modules.redis.entity.RedisMapper;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {

    @Column(name="create_time")
    @RedisMapper
    private Date createTime;
    @Column(name="create_user")
    @RedisMapper
    private String createUser;
    @Column(name="update_time")
    @RedisMapper
    private Date updateTime;
    @Column(name="update_user")
    @RedisMapper
    private String updateUser;
    @Column(name="del_tag")
    @org.hibernate.annotations.Type(type="yes_no")
    @RedisMapper
    private boolean delTag;
    @Column(name="del_reason")
    private String delReason;
    @Column(name="remark")
    @RedisMapper
    private String remark;

    @Transient
    private String delTagStr;

    public  void genBaseVariables(){
        if(createTime == null){
            createTime = new Date();
        }
        if(updateTime == null){
            updateTime = new Date();
        }
        delTag = false;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public boolean isDelTag() {
        return delTag;
    }

    public void setDelTag(boolean delTag) {
        this.delTag = delTag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDelReason() {
        return delReason;
    }

    public void setDelReason(String delReason) {
        this.delReason = delReason;
    }

    public String getDelTagStr() {
        return delTagStr;
    }

    public void setDelTagStr(String delTagStr) {
        this.delTagStr = delTagStr;
    }
}
