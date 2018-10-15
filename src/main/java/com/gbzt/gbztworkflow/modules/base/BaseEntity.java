package com.gbzt.gbztworkflow.modules.base;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {

    @Column(name="create_time")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public  void genCreateTime(){
        if(createTime == null){
            createTime = new Date();
        }
    }
}
