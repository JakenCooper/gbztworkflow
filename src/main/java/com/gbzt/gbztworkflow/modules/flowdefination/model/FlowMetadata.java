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
    //user selected table.
    private String bussTableName;
    //user selected columns.
    private List<String> bussColumns;

    private List<String> dbTables;
    private List<String> dbTableColumns;

    @Override
    public String toString() {
        StringBuffer sb= new StringBuffer();
        sb.append(bussDbType).append(" : ").append(bussDbHost).append(" , ").append(bussDbPort)
                .append(" , ").append(bussDbName)
                .append(" + ").append(bussDbUserName)
                .append(" + ").append(bussDbUserPwd);
        return sb.toString();
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
}
