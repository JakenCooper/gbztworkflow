package com.gbzt.gbztworkflow.modules.flowdefination.entity;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(name="gw_def_flow")
public class Flow extends BaseEntity {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="name")
    private String flowName;
    @Transient
    private List<Node> allNodes = new ArrayList<Node>();
    @Transient
    private Node startNode;
    @Transient
    private Node endNode;
    @Transient
    //以node的name作为key
    private Map<String,Node> nodeMap = new HashMap<String,Node>();

    @Column(name="buss_table")
    //user selected table.
    private String bussTableName;
    @Transient
    //user selected columns.
    private List<String> bussColumns;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<Node> getAllNodes() {
        return allNodes;
    }

    public void setAllNodes(List<Node> allNodes) {
        this.allNodes = allNodes;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getBussTableName() {
        return bussTableName;
    }

    public void setBussTableName(String bussTableName) {
        this.bussTableName = bussTableName;
    }

    public List<String> getBussColumns() {
        return bussColumns;
    }

    public void setBussColumns(List<String> bussColumns) {
        this.bussColumns = bussColumns;
    }
}
