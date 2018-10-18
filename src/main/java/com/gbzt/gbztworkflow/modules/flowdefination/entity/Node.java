package com.gbzt.gbztworkflow.modules.flowdefination.entity;


import com.gbzt.gbztworkflow.modules.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity(name="gw_def_node")
public class Node extends BaseEntity {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="node_def_id")
    private String nodeDefId;
    @Column(name="name")
    private String name;
    @Column(name="description")
    private String description;
    @Column(name="flow_id")
    private String flowId;
    @Column(name="is_begin_node")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean beginNode;
    @Column(name="is_end_node")
    @org.hibernate.annotations.Type(type="yes_no")
    private boolean endNode;

    @Transient
    private List<Line> outLines = new ArrayList<Line>();
    @Transient
    private List<Line> inLines = new ArrayList<Line>();
    @Transient
    private List<Node> nextNodes = new ArrayList<Node>();
    @Transient
    private List<Node> foreNodes = new ArrayList<Node>();
    @Transient
    private List<Node> allNodesInFlow = new ArrayList<Node>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public boolean isBeginNode() {
        return beginNode;
    }

    public void setBeginNode(boolean beginNode) {
        this.beginNode = beginNode;
    }

    public boolean isEndNode() {
        return endNode;
    }

    public void setEndNode(boolean endNode) {
        this.endNode = endNode;
    }

    public List<Line> getOutLines() {
        return outLines;
    }

    public void setOutLines(List<Line> outLines) {
        this.outLines = outLines;
    }

    public List<Line> getInLines() {
        return inLines;
    }

    public void setInLines(List<Line> inLines) {
        this.inLines = inLines;
    }

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(List<Node> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public List<Node> getForeNodes() {
        return foreNodes;
    }

    public void setForeNodes(List<Node> foreNodes) {
        this.foreNodes = foreNodes;
    }

    public List<Node> getAllNodesInFlow() {
        return allNodesInFlow;
    }

    public void setAllNodesInFlow(List<Node> allNodesInFlow) {
        this.allNodesInFlow = allNodesInFlow;
    }

    public String getNodeDefId() {
        return nodeDefId;
    }

    public void setNodeDefId(String nodeDefId) {
        this.nodeDefId = nodeDefId;
    }
}
