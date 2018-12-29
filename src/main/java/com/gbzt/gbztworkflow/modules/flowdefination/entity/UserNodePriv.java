package com.gbzt.gbztworkflow.modules.flowdefination.entity;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;
import com.gbzt.gbztworkflow.modules.redis.entity.RedisMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Entity(name="gw_def_user_node_priv")
public class UserNodePriv extends BaseEntity {
    @Id
    @Column(name="id")
    @RedisMapper
    private String id;
    @Column(name="node_id")
    @RedisMapper
    private String nodeId;
    @Column(name="login_name")
    @RedisMapper
    private String loginName;
    @Column(name="flow_id")
    @RedisMapper
    private String flowId;
    @Column(name="node_type")
    @RedisMapper
    private String nodeType;

    @Transient
    private List<String> loginNames;

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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public List<String> getLoginNames() {
        return loginNames;
    }

    public void setLoginNames(List<String> loginNames) {
        this.loginNames = loginNames;
    }
}
