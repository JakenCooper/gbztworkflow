package com.gbzt.gbztworkflow.modules.flowdefination.entity;


import com.gbzt.gbztworkflow.modules.base.BaseEntity;
import com.gbzt.gbztworkflow.modules.redis.entity.RedisMapper;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity(name="gw_def_node")
public class Node extends BaseEntity {
    @Id
    @Column(name="id")
    @RedisMapper
    private String id;
    @Column(name="node_def_id")
    @RedisMapper
    private String nodeDefId;
    @Column(name="name")
    @RedisMapper
    private String name;
    @Column(name="description")
    @RedisMapper
    private String description;
    @Column(name="flow_id")
    @RedisMapper
    private String flowId;
    @Column(name="is_begin_node")
    @org.hibernate.annotations.Type(type="yes_no")
    @RedisMapper
    private Boolean beginNode;
    @Column(name="is_end_node")
    @org.hibernate.annotations.Type(type="yes_no")
    @RedisMapper
    private Boolean endNode;
    @Column(name="sort_num")
    @RedisMapper
    private Integer sortNum =1;
    @Column(name="assign_user")
    @RedisMapper
    private String assignUser;
    @Transient
    private String beginNodeStr;
    @Transient
    private String endNodeStr;


    public String getBeginNodeStr() {
        return beginNodeStr;
    }

    public void setBeginNodeStr(String beginNodeStr) {
        this.beginNodeStr = beginNodeStr;
    }

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

    public Boolean getBeginNode() {
        return beginNode;
    }

    public void setBeginNode(Boolean beginNode) {
        this.beginNode = beginNode;
    }

    public Boolean getEndNode() {
        return endNode;
    }

    public void setEndNode(Boolean endNode) {
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

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public String getAssignUser() {
        return assignUser;
    }

    public void setAssignUser(String assignUser) {
        this.assignUser = assignUser;
    }

    public static List<Node> sortNodes(List<Node> nodes){
        Collections.sort(nodes,new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                if(n1.getSortNum() > n2.getSortNum()){
                    return 1;
                }else if(n1.getSortNum() < n2.getSortNum()){
                    return -1;
                }else{
                    if(StringUtils.isBlank(n1.getNodeDefId()) || StringUtils.isBlank(n2.getNodeDefId())){
                        return 0;
                    }
                    String node1DefId = (n1.getNodeDefId().split("-"))[1];
                    String node2DefId = (n2.getNodeDefId().split("-"))[1];
                    if(Integer.parseInt(node1DefId) > Integer.parseInt(node2DefId)){
                        return -1;
                    }else if(Integer.parseInt(node1DefId) < Integer.parseInt(node2DefId)){
                        return 1;
                    }else{
                        return 0;
                    }
                }
            }
        });
        return nodes;
    }

}
