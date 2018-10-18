package com.gbzt.gbztworkflow.modules.flowruntime.model;

import java.util.List;

public class DefinationModel extends BaseModel{
    private String flowId;
    private String flowName;
    private String nodeId;
    private String nodeDefId;
    private String nodeName;
    private List<DefinationModel> subModelList;


    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeDefId() {
        return nodeDefId;
    }

    public void setNodeDefId(String nodeDefId) {
        this.nodeDefId = nodeDefId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<DefinationModel> getSubModelList() {
        return subModelList;
    }

    public void setSubModelList(List<DefinationModel> subModelList) {
        this.subModelList = subModelList;
    }
}
