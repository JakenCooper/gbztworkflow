package com.gbzt.gbztworkflow.modules.flowdefination.entity;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;
import com.gbzt.gbztworkflow.modules.redis.entity.RedisMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="gw_def_flowbuss")
public class FlowBuss extends BaseEntity {
    @Id
    @Column(name="id")
    @RedisMapper
    private String id;

    @Column(name="flow_id")
    @RedisMapper
    private String flowId;

    @Column(name="column_name")
    @RedisMapper
    private String columnName;

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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
