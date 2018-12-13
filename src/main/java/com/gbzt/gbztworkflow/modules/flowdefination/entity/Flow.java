package com.gbzt.gbztworkflow.modules.flowdefination.entity;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;
import com.gbzt.gbztworkflow.modules.redis.RedisMapper;
import org.hibernate.annotations.Type;

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
    @RedisMapper
    private String id;
    @Column(name="name")
    @RedisMapper
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
    @Transient
    private Map<String,Node> nodeIdMap = new HashMap<String,Node>();

    @Transient
    private List<Line> allLines = new ArrayList<Line>();
    @Transient
    private Map<String,Line> lineMap = new HashMap<String,Line>();

    @Column(name="buss_table")
    //user selected table.
    @RedisMapper
    private String bussTableName;
    @Transient
    //user selected columns.
    private List<String> bussColumns;

    @Column(name="form_key")
    @RedisMapper
    private String formKey;

    @Column(name="buss_db_type")
    @RedisMapper
    private String bussDbType;
    @Column(name="buss_db_host")
    @RedisMapper
    private String bussDbHost;
    @Column(name="buss_db_port")
    @RedisMapper
    private String bussDbPort;
    @Column(name="buss_db_name")
    @RedisMapper
    private String bussDbName;
    @Column(name="buss_db_user_name")
    @RedisMapper
    private String bussDbUserName;
    @Column(name="buss_db_user_pwd")
    @RedisMapper
    private String bussDbUserPwd;

    @Column(name="module_name")
    @RedisMapper
    private String  moduleName;
    @Column(name="module_name_cn")
    @RedisMapper
    private String moduleNameCn;
    @Column(name="module_root_path")
    @RedisMapper
    private String moduleRootPath;

    public String getBussDbType() {
        return bussDbType;
    }

    public void setBussDbType(String bussDbType) {
        this.bussDbType = bussDbType;
    }

    public String getBussDbHost() {
        return bussDbHost;
    }

    public void setBussDbHost(String bussDbHost) {
        this.bussDbHost = bussDbHost;
    }

    public String getBussDbPort() {
        return bussDbPort;
    }

    public void setBussDbPort(String bussDbPort) {
        this.bussDbPort = bussDbPort;
    }

    public String getBussDbName() {
        return bussDbName;
    }

    public void setBussDbName(String bussDbName) {
        this.bussDbName = bussDbName;
    }

    public String getBussDbUserName() {
        return bussDbUserName;
    }

    public void setBussDbUserName(String bussDbUserName) {
        this.bussDbUserName = bussDbUserName;
    }

    public String getBussDbUserPwd() {
        return bussDbUserPwd;
    }

    public void setBussDbUserPwd(String bussDbUserPwd) {
        this.bussDbUserPwd = bussDbUserPwd;
    }

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

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public List<Line> getAllLines() {
        return allLines;
    }

    public void setAllLines(List<Line> allLines) {
        this.allLines = allLines;
    }

    public Map<String, Line> getLineMap() {
        return lineMap;
    }

    public void setLineMap(Map<String, Line> lineMap) {
        this.lineMap = lineMap;
    }

    public Map<String, Node> getNodeIdMap() {
        return nodeIdMap;
    }

    public void setNodeIdMap(Map<String, Node> nodeIdMap) {
        this.nodeIdMap = nodeIdMap;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleNameCn() {
        return moduleNameCn;
    }

    public void setModuleNameCn(String moduleNameCn) {
        this.moduleNameCn = moduleNameCn;
    }

    public String getModuleRootPath() {
        return moduleRootPath;
    }

    public void setModuleRootPath(String moduleRootPath) {
        this.moduleRootPath = moduleRootPath;
    }

}
