package com.gbzt.gbztworkflow.modules.flowdefination.model;

import java.util.List;

public class FlowMetadata {

    private String flowName;
    private String bussDbType;
    private String bussDbHost;
    private String bussDbPort;
    private String bussDbName;
    private String bussDbUserName;
    private String bussDbUserPwd;
    private String bussModelPath;
    //user selected table.
    private String bussTableName;
    //user selected columns.
    private List<String> bussColumns;
    //user selected coumns type.
    private List<String> bussColumnsType;

    private List<String> dbTables;
    private List<String> dbTableColumns;
    //附件表
    private String  attachBussTableName;//附件表名
    private List<String> attachBussColumns; //user selected columns.
    private List<String> attachBussColumnsType; //user selected coumns type.
    private String isAttach;

    public String getAttachBussTableName() {
        return attachBussTableName;
    }

    public void setAttachBussTableName(String attachBussTableName) {
        this.attachBussTableName = attachBussTableName;
    }

    public List<String> getAttachBussColumns() {
        return attachBussColumns;
    }

    public void setAttachBussColumns(List<String> attachBussColumns) {
        this.attachBussColumns = attachBussColumns;
    }

    public List<String> getAttachBussColumnsType() {
        return attachBussColumnsType;
    }

    public void setAttachBussColumnsType(List<String> attachBussColumnsType) {
        this.attachBussColumnsType = attachBussColumnsType;
    }

    public String getIsAttach() {
        return isAttach;
    }

    public void setIsAttach(String isAttach) {
        this.isAttach = isAttach;
    }

    @Override
    public String toString() {
        StringBuffer sb= new StringBuffer();
        sb.append(bussDbType).append(" : ").append(bussDbHost).append(" , ").append(bussDbPort)
                .append(" , ").append(bussDbName)
                .append(" + ").append(bussDbUserName)
                .append(" + ").append(bussDbUserPwd);
        return sb.toString();
    }


    public String getBussModelPath() {
        return bussModelPath;
    }

    public void setBussModelPath(String bussModelPath) {
        this.bussModelPath = bussModelPath;
    }

    public String getBussDbType() {
        return bussDbType;
    }

    public void setBussDbType(String bussDbType) {
        this.bussDbType = bussDbType;
    }

    public List<String> getDbTables() {
        return dbTables;
    }

    public void setDbTables(List<String> dbTables) {
        this.dbTables = dbTables;
    }

    public List<String> getDbTableColumns() {
        return dbTableColumns;
    }

    public void setDbTableColumns(List<String> dbTableColumns) {
        this.dbTableColumns = dbTableColumns;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
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

    public List<String> getBussColumnsType() {
        return bussColumnsType;
    }

    public void setBussColumnsType(List<String> bussColumnsType) {
        this.bussColumnsType = bussColumnsType;
    }
}
